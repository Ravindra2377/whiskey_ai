package com.boozer.nexus.security.repository;

import com.boozer.nexus.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * 
 * JPA repository for User entity operations with custom queries
 * for the NEXUS AI platform security system.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Find all disabled users
     */
    List<User> findByEnabledFalse();
    
    /**
     * Find users by role name
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    /**
     * Find users created after specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users who logged in after specific date
     */
    List<User> findByLastLoginAfter(LocalDateTime date);
    
    /**
     * Find locked users
     */
    List<User> findByAccountNonLockedFalse();
    
    /**
     * Count total users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
    
    /**
     * Count enabled users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countEnabledUsers();
    
    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRole(@Param("roleName") String roleName);
    
    /**
     * Find users with failed login attempts
     */
    @Query("SELECT u FROM User u WHERE u.failedAttempts > :threshold")
    List<User> findUsersWithFailedAttempts(@Param("threshold") int threshold);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}