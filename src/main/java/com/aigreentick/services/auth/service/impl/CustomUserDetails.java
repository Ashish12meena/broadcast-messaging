package com.aigreentick.services.auth.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.model.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public List<Long> getRoleId() {
        return user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList());
    }

    public List<RoleType> getRoleName() {
        return user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // or user.getUsername() if used instead
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // or check user.getStatus(), etc.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // or check user.getIsLocked()
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getDeletedAt() == null;
        // return true;
    }
}
