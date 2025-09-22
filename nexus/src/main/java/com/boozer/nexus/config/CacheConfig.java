package com.boozer.nexus.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // Define cache names for different types of data
        cacheManager.setCacheNames(Arrays.asList(
            "tenantConfigurations",
            "integrationConfigs",
            "usageSummaries",
            "systemDiscoveries"
        ));
        return cacheManager;
    }
}
