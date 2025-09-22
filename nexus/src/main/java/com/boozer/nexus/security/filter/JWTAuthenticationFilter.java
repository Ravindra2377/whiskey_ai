package com.boozer.nexus.security.filter;

import com.boozer.nexus.security.service.JWTService;
import com.boozer.nexus.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * Processes JWT tokens in request headers and establishes security context
 * for authenticated users with rate limiting and security validation.
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private UserService userService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
                String token = authHeader.substring(HEADER_PREFIX.length());
                
                if (jwtService.isTokenValid(token)) {
                    String username = jwtService.extractUsername(token);
                    
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userService.loadUserByUsername(username);
                        
                        if (jwtService.isTokenValid(token, userDetails)) {
                            // Check rate limiting
                            if (userService.checkRateLimit(username, request)) {
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                
                                logger.debug("Authenticated user {} for request to {}", username, request.getRequestURI());
                            } else {
                                logger.warn("Rate limit exceeded for user {} from IP {}", 
                                    username, getClientIpAddress(request));
                                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                                return;
                            }
                        }
                    }
                } else {
                    logger.debug("Invalid JWT token in request to {}", request.getRequestURI());
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
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
}