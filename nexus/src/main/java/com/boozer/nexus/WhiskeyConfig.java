package com.boozer.nexus;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class WhiskeyConfig {
    
    // Beans are automatically detected via @Service annotations
    // No explicit bean definitions needed here
}
