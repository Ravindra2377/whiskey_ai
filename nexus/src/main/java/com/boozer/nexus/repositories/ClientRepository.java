package com.boozer.nexus.repositories;

import com.boozer.nexus.entities.Client;
import com.boozer.nexus.entities.ClientTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);
    List<Client> findByTier(ClientTier tier);
}
