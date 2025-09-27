package com.boozer.nexus.security.config;

import com.boozer.nexus.security.filter.JWTAuthenticationFilter;
import com.boozer.nexus.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * NEXUS Security Configuration
 * 
 * Comprehensive security setup for the AI integration platform including:
 * - JWT-based authentication
 * - Role-based access control (RBAC)
 * - Rate limiting
 * - API endpoint protection
 * - CORS configuration
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strong encryption
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public endpoints
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/health").permitAll()
                .antMatchers("/api/v1/demo/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // AI Integration endpoints with role-based access
                .antMatchers("/api/v1/ai/process").hasAnyRole("USER", "ADMIN", "AI_USER")
                .antMatchers("/api/v1/ai/process/async").hasAnyRole("USER", "ADMIN", "AI_USER")
                .antMatchers("/api/v1/ai/process/batch").hasAnyRole("ADMIN", "AI_POWER_USER")
                .antMatchers("/api/v1/ai/conversation").hasAnyRole("USER", "ADMIN", "AI_USER")
                .antMatchers("/api/v1/ai/function-call").hasAnyRole("ADMIN", "AI_POWER_USER")
                
                // Provider management - Admin only
                .antMatchers("/api/v1/ai/providers/**").hasRole("ADMIN")
                .antMatchers("/api/v1/ai/config/**").hasRole("ADMIN")
                
                // Monitoring and analytics - Power users and admins
                .antMatchers("/api/v1/ai/metrics").hasAnyRole("ADMIN", "AI_POWER_USER")
                .antMatchers("/api/v1/ai/routing/analytics").hasAnyRole("ADMIN", "AI_POWER_USER")
                .antMatchers("/api/v1/ai/costs/**").hasAnyRole("ADMIN", "AI_POWER_USER")
                
                // Quantum and neuromorphic features - Restricted access
                .antMatchers("/api/v1/quantum/**").hasRole("QUANTUM_USER")
                .antMatchers("/api/v1/neuromorphic/**").hasRole("NEUROMORPHIC_USER")
                .antMatchers("/api/v1/consciousness/**").hasRole("CONSCIOUSNESS_USER")
                
                // Database operations
                .antMatchers("/api/v1/database/**").hasAnyRole("ADMIN", "DB_USER")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Allow API and actuator endpoints during development
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}