package com.boozer.nexus.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {
    @Modifying
    @Query("delete from OperationEntity o where o.workspace = :workspace")
    void deleteAllByWorkspace(@Param("workspace") WorkspaceEntity workspace);
}
