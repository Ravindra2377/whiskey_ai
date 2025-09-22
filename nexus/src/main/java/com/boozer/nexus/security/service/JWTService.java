package com.boozer.nexus.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service
 * 
 * Handles JWT token creation, validation, and extraction of claims
 * with enhanced security features and role-based access control.
 */
@Service
public class JWTService {
    
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    
    @Value("${nexus.ai.security.jwt-secret:nexus-ai-ultra-secure-secret-key-2024}")
    private String jwtSecret;
    
    @Value("${nexus.ai.security.token-expiry:3600}")
    private int jwtExpirationInSeconds;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generate JWT token for user with roles and permissions
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("type", "access_token");
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        return createToken(claims, userDetails.getUsername(), jwtExpirationInSeconds * 24); // 24x longer
    }
    
    /**
     * Generate token with custom claims
     */
    public String generateTokenWithClaims(UserDetails userDetails, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put("roles", userDetails.getAuthorities());
        claims.put("type", "access_token");
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, jwtExpirationInSeconds);
    }
    
    private String createToken(Map<String, Object> claims, String subject, int expirationInSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (expirationInSeconds * 1000L));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .setIssuer("nexus-ai-platform")
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * Validate token against user details
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Basic token validation
     */
    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public String[] extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof java.util.List) {
                java.util.List<Map<String, String>> rolesList = (java.util.List<Map<String, String>>) rolesObj;
                return rolesList.stream()
                    .map(role -> role.get("authority"))
                    .toArray(String[]::new);
            }
            return new String[0];
        } catch (Exception e) {
            logger.error("Error extracting roles from token: {}", e.getMessage());
            return new String[0];
        }
    }
    
    /**
     * Check if token has specific role
     */
    public Boolean hasRole(String token, String role) {
        String[] roles = extractRoles(token);
        for (String userRole : roles) {
            if (userRole.equals(role) || userRole.equals("ROLE_" + role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get token type (access_token or refresh_token)
     */
    public String getTokenType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (String) claims.get("type");
        } catch (Exception e) {
            logger.error("Error extracting token type: {}", e.getMessage());
            return "unknown";
        }
    }
    
    /**
     * Get remaining token validity time in seconds
     */
    public long getRemainingValidityTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long now = new Date().getTime();
            return Math.max(0, (expiration.getTime() - now) / 1000);
        } catch (Exception e) {
            logger.error("Error calculating remaining validity time: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Validate refresh token
     */
    public Boolean isRefreshTokenValid(String token) {
        try {
            return isTokenValid(token) && "refresh_token".equals(getTokenType(token));
        } catch (Exception e) {
            logger.error("Error validating refresh token: {}", e.getMessage());
            return false;
        }
    }
}