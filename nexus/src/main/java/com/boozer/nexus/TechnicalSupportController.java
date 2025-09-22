package com.boozer.nexus;

import com.boozer.nexus.agent.TechnicalTicket;
import com.boozer.nexus.agent.TechnicalSolution;
import com.boozer.nexus.service.TechnicalSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nexus/support")
public class TechnicalSupportController {
    
    @Autowired
    private TechnicalSupportService supportService;
    
    /**
     * Endpoint to submit a technical ticket
     */
    @PostMapping("/tickets")
    public ResponseEntity<Map<String, Object>> submitTicket(@RequestBody TechnicalTicket ticket) {
        try {
            // Process the ticket and generate a solution
            TechnicalSolution solution = supportService.processTicket(ticket);
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("ticketId", ticket.getTicketId());
            response.put("solution", solution);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to process ticket: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Endpoint to get ticket status
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<Map<String, Object>> getTicketStatus(@PathVariable String ticketId) {
        // In a real implementation, this would retrieve the actual ticket status
        Map<String, Object> response = new HashMap<>();
        response.put("ticketId", ticketId);
        response.put("status", "IN_PROGRESS");
        response.put("lastUpdated", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get support system information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSupportInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("name", "WHISKEY AI Technical Support System");
        response.put("version", "1.0.0");
        response.put("description", "24/7 automated technical support with specialized AI agents");
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("supportedDomains", java.util.Arrays.asList(
            "Database Optimization",
            "Cloud Infrastructure",
            "Security Analysis",
            "DevOps Automation",
            "API Integration"
        ));
        
        capabilities.put("responseTime", "Immediate for AI-handled issues, 24h for escalations");
        capabilities.put("availability", "24/7/365");
        
        response.put("capabilities", capabilities);
        
        return ResponseEntity.ok(response);
    }
}
