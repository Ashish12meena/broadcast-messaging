package com.aigreentick.services.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.Permission;

@Repository
public interface PermissionRepostory extends JpaRepository<Permission,Long>{
    boolean existsByName(String name);

    Optional<Permission>    findByName(String name);
    // List<Permission> findByRoles_Id(Long roleId); // third table many ton many
}