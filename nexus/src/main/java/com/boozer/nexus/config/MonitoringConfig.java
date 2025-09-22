package com.boozer.nexus.config;

import com.boozer.nexus.service.EnterpriseMonitoringService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {
    
    @Bean
    public EnterpriseMonitoringService enterpriseMonitoringService() {
        return new EnterpriseMonitoringService();
    }
}
