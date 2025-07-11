package com.aigreentick.services.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aigreentick.services.auth.model.Permission;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.model.UserPermission;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserAndPermission(User user, Permission permission);
}