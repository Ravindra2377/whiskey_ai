package com.boozer.nexus.repository;

import com.boozer.nexus.model.ClientTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientTenantRepository extends JpaRepository<ClientTenant, Long> {
    
    /**
     * Find client tenant by tenant ID
     */
    Optional<ClientTenant> findByTenantId(String tenantId);
    
    /**
     * Find client tenant by client name
     */
    Optional<ClientTenant> findByClientName(String clientName);
    
    /**
     * Find client tenants by subscription tier
     */
    java.util.List<ClientTenant> findBySubscriptionTier(String subscriptionTier);
    
    /**
     * Find client tenants by status
     */
    java.util.List<ClientTenant> findByStatus(String status);
}
