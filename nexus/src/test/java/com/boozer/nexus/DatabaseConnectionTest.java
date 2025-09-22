package com.boozer.nexus;

import com.boozer.nexus.entities.Client;
import com.boozer.nexus.entities.ClientTier;
import com.boozer.nexus.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class DatabaseConnectionTest {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Test
    public void testDatabaseConnection() {
        Client testClient = new Client();
        testClient.setClientId("test-client-" + System.currentTimeMillis());
        testClient.setCompanyName("Test Company");
        testClient.setContactEmail("test@example.com");
        testClient.setTier(ClientTier.STARTER);
        
        Client savedClient = clientRepository.save(testClient);
        assertThat(savedClient.getId()).isNotNull();
        
        System.out.println("✅ Database connection successful!");
        System.out.println("Created client: " + savedClient.getCompanyName());
    }
}
