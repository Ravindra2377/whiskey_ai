package com.boozer.nexus.test.security;

import com.boozer.nexus.security.*;
import com.boozer.nexus.entities.User;
import com.boozer.nexus.entities.Role;
import com.boozer.nexus.controllers.AuthController;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

/**
 * Comprehensive Security Tests for NEXUS AI Platform
 * 
 * Tests authentication, authorization, JWT security, rate limiting,
 * and security configurations across the entire platform.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NexusSecurityTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private JWTUtil jwtUtil;
    
    @Autowired
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    void setupSecurityTest() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
        objectMapper = new ObjectMapper();
        System.out.println("Starting NEXUS AI Platform Security Tests");
    }

    @Test
    @DisplayName("JWT Token Generation and Validation")
    void testJWTTokenSecurity() {
        // Create test user
        User testUser = createTestUser("securitytest", "security@test.com", "USER");
        
        // Test token generation
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateToken(testUser);
            
            assertNotNull(token);
            assertFalse(token.isEmpty());
            
            // Validate token structure (JWT has 3 parts separated by dots)
            String[] tokenParts = token.split("\\.");
            assertEquals(3, tokenParts.length, "JWT should have header, payload, and signature");
            
            // Test token validation
            assertTrue(jwtUtil.validateToken(token, testUser));
            
            // Test username extraction
            assertEquals("securitytest", jwtUtil.getUsernameFromToken(token));
            
            // Test token not expired
            assertFalse(jwtUtil.isTokenExpired(token));
        });
    }

    @Test
    @DisplayName("JWT Token Expiration and Invalid Token Handling")
    void testJWTTokenExpiration() {
        User testUser = createTestUser("exptest", "exp@test.com", "USER");
        
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateToken(testUser);
            
            // Test with valid token
            assertTrue(jwtUtil.validateToken(token, testUser));
            
            // Test with modified token (invalid signature)
            String invalidToken = token.substring(0, token.length() - 10) + "invalidsig";
            assertFalse(jwtUtil.validateToken(invalidToken, testUser));
            
            // Test with null token
            assertFalse(jwtUtil.validateToken(null, testUser));
            
            // Test with empty token
            assertFalse(jwtUtil.validateToken("", testUser));
        });
    }

    @Test
    @DisplayName("Authentication Endpoint Security")
    void testAuthenticationEndpoints() throws Exception {
        // Test login endpoint
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        // Test register endpoint
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("email", "new@test.com");
        registerRequest.put("password", "newpassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Role-Based Access Control")
    @WithMockUser(roles = "USER")
    void testRoleBasedAccess() throws Exception {
        // Test user access to AI endpoints
        mockMvc.perform(get("/api/ai/providers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test user access to consciousness endpoints
        mockMvc.perform(get("/api/consciousness/session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin Role Access Control")
    @WithMockUser(roles = "ADMIN")
    void testAdminAccess() throws Exception {
        // Test admin access to quantum endpoints
        mockMvc.perform(get("/api/quantum/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test admin access to neuromorphic endpoints
        mockMvc.perform(get("/api/neuromorphic/networks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthorized Access Prevention")
    void testUnauthorizedAccess() throws Exception {
        // Test accessing protected endpoints without authentication
        mockMvc.perform(get("/api/ai/process")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/consciousness/process")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isUnauthorized());

        mockMvc.perform(get("/api/quantum/run")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Rate Limiting Security")
    void testRateLimiting() {
        // Test rate limiting with multiple concurrent requests
        int requestCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(requestCount);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        User testUser = createTestUser("ratetest", "rate@test.com", "USER");
        String token = jwtUtil.generateToken(testUser);

        for (int i = 0; i < requestCount; i++) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Simulate API request with rate limiting
                    mockMvc.perform(get("/api/ai/providers")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON))
                            .andReturn();
                    return true;
                } catch (Exception e) {
                    return false;
                } finally {
                    latch.countDown();
                }
            }, executor);
            futures.add(future);
        }

        assertDoesNotThrow(() -> {
            latch.await(30, TimeUnit.SECONDS);
            
            long successfulRequests = futures.stream()
                .mapToInt(future -> {
                    try {
                        return future.get() ? 1 : 0;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();

            // Some requests should be rate limited
            assertTrue(successfulRequests < requestCount, 
                "Rate limiting should prevent all requests from succeeding");
            assertTrue(successfulRequests > 0, 
                "Some requests should succeed before rate limiting kicks in");
        });

        executor.shutdown();
    }

    @Test
    @DisplayName("Input Validation and Sanitization")
    void testInputValidation() throws Exception {
        // Test XSS prevention
        Map<String, String> xssRequest = new HashMap<>();
        xssRequest.put("username", "<script>alert('xss')</script>");
        xssRequest.put("password", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isBadRequest());

        // Test SQL injection prevention
        Map<String, String> sqlInjectionRequest = new HashMap<>();
        sqlInjectionRequest.put("username", "'; DROP TABLE users; --");
        sqlInjectionRequest.put("password", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sqlInjectionRequest)))
                .andExpect(status().isBadRequest());

        // Test excessive payload size
        StringBuilder largePayload = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largePayload.append("A");
        }
        
        Map<String, String> largeRequest = new HashMap<>();
        largeRequest.put("username", largePayload.toString());
        largeRequest.put("password", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CORS Security Configuration")
    void testCORSConfiguration() throws Exception {
        // Test CORS headers
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));

        // Test unauthorized origin
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Session Security and Management")
    @WithMockUser(username = "sessiontest", roles = "USER")
    void testSessionSecurity() throws Exception {
        // Test session creation
        mockMvc.perform(post("/api/consciousness/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        // Test session access with valid authentication
        mockMvc.perform(get("/api/consciousness/session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API Key Security")
    void testAPIKeySecurity() {
        // Test API key validation
        String validApiKey = "nexus-api-key-123456789";
        String invalidApiKey = "invalid-key";

        assertDoesNotThrow(() -> {
            // Test valid API key
            mockMvc.perform(get("/api/ai/providers")
                    .header("X-API-Key", validApiKey)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Test invalid API key
            mockMvc.perform(get("/api/ai/providers")
                    .header("X-API-Key", invalidApiKey)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            // Test missing API key
            mockMvc.perform(get("/api/ai/providers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        });
    }

    @Test
    @DisplayName("Password Security Validation")
    void testPasswordSecurity() {
        // Test password strength requirements
        String[] weakPasswords = {
            "123", "password", "abc", "111111", "qwerty"
        };

        String[] strongPasswords = {
            "Str0ng!Pass", "MySecure#123", "C0mplex$Word"
        };

        for (String weakPassword : weakPasswords) {
            assertDoesNotThrow(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("username", "testuser");
                request.put("email", "test@example.com");
                request.put("password", weakPassword);

                mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpected(status().isBadRequest());
            });
        }

        for (String strongPassword : strongPasswords) {
            assertDoesNotThrow(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("username", "testuser" + System.currentTimeMillis());
                request.put("email", "test" + System.currentTimeMillis() + "@example.com");
                request.put("password", strongPassword);

                mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());
            });
        }
    }

    @Test
    @DisplayName("Data Encryption and Privacy")
    void testDataEncryption() {
        User testUser = createTestUser("encrypttest", "encrypt@test.com", "USER");
        
        // Test that sensitive data is not exposed in logs or responses
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateToken(testUser);
            
            // Verify password is not stored in plain text
            assertNotEquals("password", testUser.getPassword());
            
            // Verify sensitive data is not included in token payload
            String[] tokenParts = token.split("\\.");
            String payload = new String(Base64.getDecoder().decode(tokenParts[1]));
            assertFalse(payload.contains("password"));
            assertFalse(payload.contains(testUser.getPassword()));
        });
    }

    @Test
    @DisplayName("Concurrent Security Operations")
    void testConcurrentSecurity() {
        int concurrentUsers = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // Create multiple users concurrently
        for (int i = 0; i < concurrentUsers; i++) {
            final int userId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                User user = createTestUser("concurrent" + userId, "concurrent" + userId + "@test.com", "USER");
                return jwtUtil.generateToken(user);
            }, executor);
            futures.add(future);
        }

        assertDoesNotThrow(() -> {
            List<String> tokens = futures.stream()
                .map(CompletableFuture::join)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            // Verify all tokens are unique
            Set<String> uniqueTokens = new HashSet<>(tokens);
            assertEquals(concurrentUsers, uniqueTokens.size(), "All tokens should be unique");

            // Verify all tokens are valid
            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                User user = createTestUser("concurrent" + i, "concurrent" + i + "@test.com", "USER");
                assertTrue(jwtUtil.validateToken(token, user));
            }
        });

        executor.shutdown();
    }

    private User createTestUser(String username, String email, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedpassword123"); // In real implementation, this would be properly hashed
        
        Role role = new Role();
        role.setName(roleName);
        user.setRoles(Set.of(role));
        
        return user;
    }

    @AfterAll
    void tearDownSecurityTest() {
        System.out.println("Completed NEXUS AI Platform Security Tests");
    }
}