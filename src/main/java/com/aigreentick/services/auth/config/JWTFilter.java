package com.aigreentick.services.auth.config;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.repository.UserRepository;
import com.aigreentick.services.auth.service.impl.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWTFilter intercepts incoming HTTP requests, extracts and validates the JWT
 * token,
 * loads user details, and sets the authentication in the SecurityContext.
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TokenProvider jwtService;
    private final UserRepository userRepository;

    /**
     * Constructor injection for required dependencies.
     */
    public JWTFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            TokenProvider jwtService,
            UserRepository userRepository) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Called on every HTTP request. Authenticates the user if a valid JWT token is
     * present.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // If no Bearer token, continue without authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(authHeader);
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenExpired(token)) {
                    throw new RuntimeException("JWT Token expired");
                }

                Long userId = jwtService.extractUserId(token);
                User user = userRepository.findByIdWithRolesAndPermissions(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toSet());

                user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName().name())));

                CustomUserDetails userDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    /**
     * Extracts the JWT token from the Authorization header.
     */
    private String extractToken(String authHeader) {
        return authHeader.substring(7); // Remove "Bearer "
    }

    /**
     * Skips filter execution for public or static endpoints.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/public")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/image")
                || path.startsWith("/public")
                || path.equals("/swagger-ui.html")
                || path.endsWith(".js")
                || path.endsWith(".css");
    }
}
