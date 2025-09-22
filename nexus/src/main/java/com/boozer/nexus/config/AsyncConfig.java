package com.boozer.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "nexusTaskExecutor")
    public Executor nexusTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Optimize thread pool size based on available processors
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Nexus-Async-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "billingTaskExecutor")
    public Executor billingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Dedicated thread pool for billing operations
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("Billing-Async-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "integrationTaskExecutor")
    public Executor integrationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Dedicated thread pool for integration operations
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 3;
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("Integration-Async-");
        executor.initialize();
        return executor;
    }
}