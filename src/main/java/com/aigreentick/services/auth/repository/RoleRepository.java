package com.aigreentick.services.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(RoleType name);
    
    Optional<Role> findById(Integer id); // Uncomment if you need to find by ID
    
    boolean existsByName(String name);
    
}