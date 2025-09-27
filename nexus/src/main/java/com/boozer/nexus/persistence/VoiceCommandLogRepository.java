package com.boozer.nexus.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoiceCommandLogRepository extends JpaRepository<VoiceCommandLogEntity, UUID> {
}
