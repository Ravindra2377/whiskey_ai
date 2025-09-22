package com.boozer.nexus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SimpleApplicationTest {

    @Test
    public void testApplicationStarts() {
        // This test just verifies that the application context loads
        // without requiring a real database connection
        assertTrue(true, "Application context should load successfully");
    }
}
