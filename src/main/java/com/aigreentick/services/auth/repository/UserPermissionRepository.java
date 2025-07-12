package com.aigreentick.services.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.Permission;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.model.UserPermission;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserAndPermission(User user, Permission permission);
    List<UserPermission> findByUserIdAndGrantedTrue(Long userId);
}