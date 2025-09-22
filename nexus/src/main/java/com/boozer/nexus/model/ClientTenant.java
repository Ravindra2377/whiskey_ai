package com.boozer.nexus.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_tenants")
public class ClientTenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;
    
    @Column(name = "client_name", nullable = false)
    private String clientName;
    
    @Column(name = "client_email")
    private String clientEmail;
    
    @Column(name = "client_industry")
    private String clientIndustry;
    
    @Column(name = "subscription_tier")
    private String subscriptionTier;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "company_size")
    private String companySize;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ClientTenant() {}
    
    public ClientTenant(String tenantId, String clientName, String clientEmail, String clientIndustry, 
                       String subscriptionTier, String contactPerson, String companySize) {
        this.tenantId = tenantId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientIndustry = clientIndustry;
        this.subscriptionTier = subscriptionTier;
        this.contactPerson = contactPerson;
        this.companySize = companySize;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getClientIndustry() {
        return clientIndustry;
    }
    
    public void setClientIndustry(String clientIndustry) {
        this.clientIndustry = clientIndustry;
    }
    
    public String getSubscriptionTier() {
        return subscriptionTier;
    }
    
    public void setSubscriptionTier(String subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getCompanySize() {
        return companySize;
    }
    
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
