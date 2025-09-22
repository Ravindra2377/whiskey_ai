package com.boozer.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.boozer.nexus", 
    "com.boozer.nexus.enhanced", 
    "com.boozer.nexus.connectors", 
    "com.boozer.nexus.platform", 
    "com.boozer.nexus.support", 
    "com.boozer.nexus.billing",
    "com.boozer.nexus.config",
    "com.boozer.nexus.performance",
    "com.boozer.nexus.quantum",
    "com.boozer.nexus.neuromorphic",
    "com.boozer.nexus.consciousness",
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
    
    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
    }
}