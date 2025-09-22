package com.boozer.nexus.repositories;

import com.boozer.nexus.entities.TechnicalTicket;
import com.boozer.nexus.entities.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TechnicalTicketRepository extends JpaRepository<TechnicalTicket, Long> {
    Optional<TechnicalTicket> findByTicketId(String ticketId);
    List<TechnicalTicket> findByClientIdAndStatus(String clientId, TicketStatus status);
}
