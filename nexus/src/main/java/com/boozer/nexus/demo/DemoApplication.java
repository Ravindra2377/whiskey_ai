package com.boozer.nexus.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(scanBasePackages = {"com.boozer.nexus.demo"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DemoApplication.class);
        Map<String, Object> defaults = new HashMap<>();
        // Run on the same port the frontend expects by default
        defaults.put("server.port", "8094");
        // Keep actuator endpoints usable
        defaults.put("management.endpoints.web.exposure.include", "health,info");
        // Disable DB/Redis/JPA so this can run without external services
        defaults.put("spring.autoconfigure.exclude",
                String.join(",",
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
                        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
                )
        );
        app.setDefaultProperties(defaults);
        app.run(args);
    }
}
