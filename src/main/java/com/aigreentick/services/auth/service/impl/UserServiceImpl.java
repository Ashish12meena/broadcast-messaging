package com.aigreentick.services.auth.service.impl;


import java.util.Optional;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.repository.UserRepository;
import com.aigreentick.services.auth.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // Get the currently authenticated user.
    public CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("User not authenticated");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }

    public User findByEmail(String email) {

        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));
    }

    public boolean isMobileNumberExist(String mobileNumber) {
        if (mobileNumber == null)
            return false;
        return userRepository.existsByMobileNumber(mobileNumber);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getReferenceById(Long userId) {
        return userRepository.getReferenceById(userId);
    }

   

}
