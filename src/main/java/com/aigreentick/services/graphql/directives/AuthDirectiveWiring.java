package com.aigreentick.services.graphql.directives;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthDirectiveWiring implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        DataFetcher<?> originalDataFetcher = env.getFieldDataFetcher();

        List<String> allowedRoles = extractRoles(env);

        DataFetcher<?> authDataFetcher = dataFetchingEnvironment -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AccessDeniedException("Unauthenticated: No active security context.");
            }

            List<String> userRoles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            if (allowedRoles.stream().noneMatch(userRoles::contains)) {
                throw new AccessDeniedException("Access denied: insufficient permissions.");
            }

            return originalDataFetcher.get(dataFetchingEnvironment);
        };

        env.setFieldDataFetcher(authDataFetcher);
        return env.getElement();
    }

    private List<String> extractRoles(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        var directive = env.getAppliedDirective();
        if (directive == null || directive.getArgument("role") == null) {
            return Collections.emptyList();
        }

        Object value = directive.getArgument("role")
                .getArgumentValue()
                .getValue();

        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
