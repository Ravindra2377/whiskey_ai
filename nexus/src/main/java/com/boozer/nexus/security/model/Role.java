package com.boozer.nexus.security.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Role Entity
 * 
 * JPA entity representing a user role in the NEXUS AI platform
 * for role-based access control (RBAC).
 */
@Entity
@Table(name = "nexus_roles",
       indexes = {
           @Index(name = "idx_role_name", columnList = "name")
       })
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @Column(name = "is_system_role", nullable = false)
    private boolean systemRole = false;
    
    // Constructors
    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Role(String name, String description, boolean systemRole) {
        this.name = name;
        this.description = description;
        this.systemRole = systemRole;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isSystemRole() {
        return systemRole;
    }
    
    public void setSystemRole(boolean systemRole) {
        this.systemRole = systemRole;
    }
    
    // Utility methods
    public String getSimpleName() {
        if (name.startsWith("ROLE_")) {
            return name.substring(5);
        }
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", systemRole=" + systemRole +
                '}';
    }
    
    // Predefined roles as constants
    public static final Role ADMIN = new Role("ROLE_ADMIN", "Full system access", true);
    public static final Role USER = new Role("ROLE_USER", "Basic user access", true);
    public static final Role AI_USER = new Role("ROLE_AI_USER", "AI service access", true);
    public static final Role AI_POWER_USER = new Role("ROLE_AI_POWER_USER", "Advanced AI features", true);
    public static final Role QUANTUM_USER = new Role("ROLE_QUANTUM_USER", "Quantum computing access", true);
    public static final Role NEUROMORPHIC_USER = new Role("ROLE_NEUROMORPHIC_USER", "Neuromorphic processing access", true);
    public static final Role CONSCIOUSNESS_USER = new Role("ROLE_CONSCIOUSNESS_USER", "Consciousness engine access", true);
    public static final Role DB_USER = new Role("ROLE_DB_USER", "Database access", true);
}