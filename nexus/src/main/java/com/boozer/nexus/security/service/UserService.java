package com.boozer.nexus.security.service;

import com.boozer.nexus.security.model.User;
import com.boozer.nexus.security.model.Role;
import com.boozer.nexus.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User Service
 * 
 * Handles user authentication, authorization, rate limiting, and user management
 * for the NEXUS AI platform with role-based access control.
 */
@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${nexus.ai.security.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Value("${nexus.ai.security.rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;
    
    @Value("${nexus.ai.security.rate-limit.requests-per-day:10000}")
    private int requestsPerDay;
    
    // Rate limiting storage
    private final Map<String, List<LocalDateTime>> userRequestHistory = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastCleanup = new ConcurrentHashMap<>();
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            logger.debug("User {} loaded successfully with {} roles", 
                username, user.getRoles().size());
            
            return user;
        } else {
            logger.warn("User {} not found", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Create new user with roles
     */
    public User createUser(String username, String email, String password, Set<Role> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists: " + username);
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        logger.info("Created new user: {} with roles: {}", username, 
            roles.stream().map(Role::getName).toArray());
        
        return savedUser;
    }
    
    /**
     * Create admin user with all permissions
     */
    public User createAdminUser(String username, String email, String password) {
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(new Role("ROLE_ADMIN", "Full system access"));
        adminRoles.add(new Role("ROLE_AI_USER", "AI service access"));
        adminRoles.add(new Role("ROLE_AI_POWER_USER", "Advanced AI features"));
        adminRoles.add(new Role("ROLE_QUANTUM_USER", "Quantum computing access"));
        adminRoles.add(new Role("ROLE_NEUROMORPHIC_USER", "Neuromorphic processing access"));
        adminRoles.add(new Role("ROLE_CONSCIOUSNESS_USER", "Consciousness engine access"));
        adminRoles.add(new Role("ROLE_DB_USER", "Database access"));
        
        return createUser(username, email, password, adminRoles);
    }
    
    /**
     * Create AI user with standard AI permissions
     */
    public User createAIUser(String username, String email, String password) {
        Set<Role> aiRoles = new HashSet<>();
        aiRoles.add(new Role("ROLE_USER", "Basic user access"));
        aiRoles.add(new Role("ROLE_AI_USER", "AI service access"));
        
        return createUser(username, email, password, aiRoles);
    }
    
    /**
     * Update user roles
     */
    public User updateUserRoles(String username, Set<Role> newRoles) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoles(newRoles);
            User savedUser = userRepository.save(user);
            
            logger.info("Updated roles for user {}: {}", username, 
                newRoles.stream().map(Role::getName).toArray());
            
            return savedUser;
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Check rate limiting for user
     */
    public boolean checkRateLimit(String username, HttpServletRequest request) {
        String userKey = username + ":" + getClientIpAddress(request);
        
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> requests = userRequestHistory.computeIfAbsent(userKey, k -> new ArrayList<>());
        
        // Clean old requests periodically
        if (shouldCleanup(userKey, now)) {
            cleanupOldRequests(requests, now);
            lastCleanup.put(userKey, now);
        }
        
        // Check rate limits
        if (!checkMinuteLimit(requests, now) || 
            !checkHourLimit(requests, now) || 
            !checkDayLimit(requests, now)) {
            
            logger.warn("Rate limit exceeded for user {} from IP {}", 
                username, getClientIpAddress(request));
            return false;
        }
        
        // Add current request
        requests.add(now);
        
        return true;
    }
    
    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("username", user.getUsername());
            stats.put("email", user.getEmail());
            stats.put("roles", user.getRoles().stream().map(Role::getName).toArray());
            stats.put("enabled", user.isEnabled());
            stats.put("createdAt", user.getCreatedAt());
            stats.put("lastLogin", user.getLastLogin());
            stats.put("requestCount", getUserRequestCount(username));
            
            return stats;
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Disable user account
     */
    public void disableUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(false);
            userRepository.save(user);
            
            logger.info("Disabled user account: {}", username);
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Enable user account
     */
    public void enableUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
            
            logger.info("Enabled user account: {}", username);
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Change user password
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                
                logger.info("Password changed for user: {}", username);
            } else {
                throw new RuntimeException("Invalid old password");
            }
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
    
    /**
     * Get all users (admin only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Check if user has specific role
     */
    public boolean hasRole(String username, String roleName) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName) || 
                                role.getName().equals("ROLE_" + roleName));
        }
        
        return false;
    }
    
    // Private helper methods
    
    private boolean shouldCleanup(String userKey, LocalDateTime now) {
        LocalDateTime lastCleanupTime = lastCleanup.get(userKey);
        return lastCleanupTime == null || 
               ChronoUnit.MINUTES.between(lastCleanupTime, now) >= 5;
    }
    
    private void cleanupOldRequests(List<LocalDateTime> requests, LocalDateTime now) {
        requests.removeIf(requestTime -> 
            ChronoUnit.DAYS.between(requestTime, now) > 1);
    }
    
    private boolean checkMinuteLimit(List<LocalDateTime> requests, LocalDateTime now) {
        long recentRequests = requests.stream()
            .filter(requestTime -> ChronoUnit.SECONDS.between(requestTime, now) <= 60)
            .count();
        
        return recentRequests < requestsPerMinute;
    }
    
    private boolean checkHourLimit(List<LocalDateTime> requests, LocalDateTime now) {
        long recentRequests = requests.stream()
            .filter(requestTime -> ChronoUnit.HOURS.between(requestTime, now) <= 1)
            .count();
        
        return recentRequests < requestsPerHour;
    }
    
    private boolean checkDayLimit(List<LocalDateTime> requests, LocalDateTime now) {
        long recentRequests = requests.stream()
            .filter(requestTime -> ChronoUnit.DAYS.between(requestTime, now) <= 1)
            .count();
        
        return recentRequests < requestsPerDay;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private long getUserRequestCount(String username) {
        String userPattern = username + ":";
        return userRequestHistory.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(userPattern))
            .mapToLong(entry -> entry.getValue().size())
            .sum();
    }
}