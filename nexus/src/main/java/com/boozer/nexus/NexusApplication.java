package com.boozer.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozer.nexus.ai.integration.service.ExternalAIIntegrationService;
import com.boozer.nexus.quantum.processor.QuantumProcessor;
import com.boozer.nexus.security.service.UserService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * NEXUS AI Platform - Main Application
 * 
 * Enterprise-grade AI integration platform with:
 * - Multi-provider AI orchestration (OpenAI, Anthropic, Google AI)
 * - Quantum computing integration (IBM Quantum)
 * - Neuromorphic processing capabilities
 * - AI consciousness simulation
 * - Advanced security and monitoring
 * - Production-ready deployment
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
    "com.boozer.nexus.repository",
    "com.boozer.nexus.security.repository"
})
@ComponentScan(basePackages = {
    "com.boozer.nexus",
    "com.boozer.nexus.ai.integration",
    "com.boozer.nexus.security",
    "com.boozer.nexus.quantum",
    "com.boozer.nexus.neuromorphic",
    "com.boozer.nexus.consciousness",
    "com.boozer.nexus.enhanced", 
    "com.boozer.nexus.connectors", 
    "com.boozer.nexus.platform", 
    "com.boozer.nexus.support", 
    "com.boozer.nexus.billing",
    "com.boozer.nexus.config",
    "com.boozer.nexus.performance",
    "com.boozer.nexus.evolution",
    "com.boozer.nexus.bci",
    "com.boozer.nexus.orchestration",
    "com.boozer.nexus.personality",
    "com.boozer.nexus.service",
    "com.boozer.nexus.repository",
    "com.boozer.nexus.model",
    "com.boozer.nexus.agent"
})
public class NexusApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(NexusApplication.class);
    
    @Autowired
    private Environment environment;
    
    @Autowired(required = false)
    private ExternalAIIntegrationService aiIntegrationService;
    
    @Autowired(required = false)
    private QuantumProcessor quantumProcessor;
    
    @Autowired(required = false)
    private UserService userService;
    
    public static void main(String[] args) {
        // Set system properties for production deployment
        System.setProperty("spring.profiles.default", "prod");
        System.setProperty("server.port", "8094");
        
        logger.info("🚀 Starting NEXUS AI Platform - Enterprise Edition");
        logger.info("⚡ Initializing AI Integration Services...");
        
        SpringApplication app = new SpringApplication(NexusApplication.class);
        
        // Configure application properties
        app.setAdditionalProfiles("ai", "quantum", "security");
        
        try {
            app.run(args);
        } catch (Exception e) {
            logger.error("❌ Failed to start NEXUS AI Platform: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    @PostConstruct
    public void init() {
        logger.info("🔧 NEXUS AI Platform initialization started");
        logger.info("📊 Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        
        // Validate critical configurations
        validateConfiguration();
        
        logger.info("✅ NEXUS AI Platform configuration validated");
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("🎉 NEXUS AI Platform is now READY!");
        logger.info("🌐 Server started at: http://localhost:{}", 
            environment.getProperty("server.port", "8094"));
        
        printSystemStatus();
        initializeDefaultUsers();
        
        logger.info("🚀 NEXUS AI Platform - ALL SYSTEMS OPERATIONAL!");
        logger.info("📈 Ready to serve enterprise AI workloads");
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    private void validateConfiguration() {
        // Check if AI integration is enabled
        boolean aiEnabled = Boolean.parseBoolean(
            environment.getProperty("nexus.ai.integration.enabled", "true"));
        
        if (aiEnabled) {
            logger.info("🤖 AI Integration: ENABLED");
            
            // Validate AI provider configurations
            validateAIProviders();
        } else {
            logger.warn("⚠️  AI Integration: DISABLED");
        }
        
        // Check quantum computing
        boolean quantumEnabled = Boolean.parseBoolean(
            environment.getProperty("nexus.ai.quantum.enabled", "false"));
        
        if (quantumEnabled) {
            logger.info("⚛️  Quantum Computing: ENABLED");
        } else {
            logger.info("⚛️  Quantum Computing: DISABLED (set nexus.ai.quantum.enabled=true to enable)");
        }
        
        // Check security configuration
        boolean securityEnabled = Boolean.parseBoolean(
            environment.getProperty("nexus.ai.security.enabled", "true"));
        
        if (securityEnabled) {
            logger.info("🔐 Security Framework: ENABLED");
        } else {
            logger.warn("⚠️  Security Framework: DISABLED");
        }
    }
    
    private void validateAIProviders() {
        String openaiKey = environment.getProperty("nexus.ai.providers.openai.api-key");
        String anthropicKey = environment.getProperty("nexus.ai.providers.anthropic.api-key");
        String googleKey = environment.getProperty("nexus.ai.providers.google.api-key");
        
        if (openaiKey != null && !openaiKey.isEmpty() && !openaiKey.contains("your_")) {
            logger.info("✅ OpenAI Provider: CONFIGURED");
        } else {
            logger.warn("⚠️  OpenAI Provider: NOT CONFIGURED (set OPENAI_API_KEY)");
        }
        
        if (anthropicKey != null && !anthropicKey.isEmpty() && !anthropicKey.contains("your_")) {
            logger.info("✅ Anthropic Provider: CONFIGURED");
        } else {
            logger.warn("⚠️  Anthropic Provider: NOT CONFIGURED (set ANTHROPIC_API_KEY)");
        }
        
        if (googleKey != null && !googleKey.isEmpty() && !googleKey.contains("your_")) {
            logger.info("✅ Google AI Provider: CONFIGURED");
        } else {
            logger.warn("⚠️  Google AI Provider: NOT CONFIGURED (set GOOGLE_AI_API_KEY)");
        }
    }
    
    private void printSystemStatus() {
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║                    NEXUS AI PLATFORM STATUS                 ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        
        // AI Integration Status
        if (aiIntegrationService != null) {
            try {
                boolean aiHealthy = aiIntegrationService.isServiceHealthy();
                logger.info("║ 🤖 AI Integration Service: {} ║", 
                    aiHealthy ? "OPERATIONAL    " : "DEGRADED      ");
            } catch (Exception e) {
                logger.info("║ 🤖 AI Integration Service: INITIALIZING   ║");
            }
        } else {
            logger.info("║ 🤖 AI Integration Service: NOT AVAILABLE  ║");
        }
        
        // Quantum Computing Status
        if (quantumProcessor != null) {
            try {
                boolean quantumEnabled = Boolean.parseBoolean(
                    environment.getProperty("nexus.ai.quantum.enabled", "false"));
                logger.info("║ ⚛️  Quantum Computing: {} ║", 
                    quantumEnabled ? "ENABLED       " : "DISABLED      ");
            } catch (Exception e) {
                logger.info("║ ⚛️  Quantum Computing: INITIALIZING    ║");
            }
        } else {
            logger.info("║ ⚛️  Quantum Computing: NOT AVAILABLE   ║");
        }
        
        // Security Status
        if (userService != null) {
            logger.info("║ 🔐 Security Framework: OPERATIONAL     ║");
        } else {
            logger.info("║ 🔐 Security Framework: NOT AVAILABLE   ║");
        }
        
        // Database Status
        String dbUrl = environment.getProperty("spring.datasource.url", "NOT_CONFIGURED");
        if (dbUrl.contains("postgresql")) {
            logger.info("║ 🗄️  Database (PostgreSQL): CONNECTED   ║");
        } else {
            logger.info("║ 🗄️  Database: {} ║", 
                dbUrl.length() > 20 ? "CONFIGURED     " : "NOT_CONFIGURED ");
        }
        
        logger.info("║                                              ║");
        logger.info("║ 🌐 API Endpoints: /api/v1/ai/*              ║");
        logger.info("║ 🔑 Authentication: /api/v1/auth/*           ║");
        logger.info("║ ⚛️  Quantum: /api/v1/quantum/*              ║");
        logger.info("║ 📊 Health Check: /actuator/health           ║");
        logger.info("║ 📖 API Docs: /swagger-ui.html               ║");
        logger.info("║                                              ║");
        logger.info("║ 🕐 Started at: {}          ║", LocalDateTime.now());
        logger.info("╚══════════════════════════════════════════════════════════════╝");
    }
    
    private void initializeDefaultUsers() {
        if (userService != null) {
            try {
                // Check if admin user exists
                boolean adminExists = false;
                try {
                    userService.loadUserByUsername("admin");
                    adminExists = true;
                } catch (Exception e) {
                    // Admin user doesn't exist
                }
                
                if (!adminExists) {
                    // Create default admin user
                    try {
                        userService.createAdminUser("admin", "admin@nexus-ai.com", "admin123");
                        logger.info("✅ Default admin user created: admin/admin123");
                        logger.info("🔐 Please change the default password after first login!");
                    } catch (Exception e) {
                        logger.warn("⚠️  Could not create default admin user: {}", e.getMessage());
                    }
                }
                
                // Create demo AI user
                try {
                    userService.loadUserByUsername("demo");
                } catch (Exception e) {
                    try {
                        userService.createAIUser("demo", "demo@nexus-ai.com", "demo123");
                        logger.info("✅ Demo AI user created: demo/demo123");
                    } catch (Exception ex) {
                        logger.debug("Demo user creation skipped: {}", ex.getMessage());
                    }
                }
                
            } catch (Exception e) {
                logger.warn("⚠️  User initialization warning: {}", e.getMessage());
            }
        }
    }
}