package com.aigreentick.services.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.Role;
import com.aigreentick.services.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // boolean existsByEmail(String email);

    // Optional<User> findByEmail(String email);

    // use left join instead of inner join so it can not return null
    @Query("""
                SELECT DISTINCT u FROM User u
                LEFT JOIN FETCH u.roles r
                LEFT JOIN FETCH r.permissions
                WHERE u.id = :id AND u.deletedAt IS NULL
            """)
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") Long id);

    @Query("""
                SELECT
                    SUM(CASE WHEN u.mobileNumber = :mobile THEN 1 ELSE 0 END),
                    SUM(CASE WHEN u.username = :username THEN 1 ELSE 0 END)
                FROM User u
            """)
    Object checkMobileAndUsernameExist(@Param("mobile") String mobile, @Param("username") String username);

    boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByMobileNumber(String mobileNumber);

    @Query("""
                SELECT u FROM User u
                LEFT JOIN FETCH u.roles r
                LEFT JOIN FETCH r.permissions
                WHERE u.id = :userId
            """)
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);

}
