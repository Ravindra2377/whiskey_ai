package com.boozer.nexus.repositories;

import com.boozer.nexus.entities.AIAgent;
import com.boozer.nexus.entities.AgentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AIAgentRepository extends JpaRepository<AIAgent, Long> {
    Optional<AIAgent> findByAgentId(String agentId);
    List<AIAgent> findByAgentType(AgentType agentType);
}
