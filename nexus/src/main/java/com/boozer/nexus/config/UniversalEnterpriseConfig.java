package com.boozer.nexus.config;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniversalEnterpriseConfig {
    
    @Bean
    public UniversalEnterpriseIntegrationEngine universalEnterpriseIntegrationEngine() {
        return new UniversalEnterpriseIntegrationEngine();
    }
}
