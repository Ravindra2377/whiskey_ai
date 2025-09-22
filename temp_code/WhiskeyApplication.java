package com.boozer.whiskey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.boozer.whiskey")
public class WhiskeyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WhiskeyApplication.class, args);
    }
}