package com.boozer.whiskey;

import com.boozer.whiskey.model.BoozerFileEntity;
import com.boozer.whiskey.service.BoozerFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/whiskey/boozer-files")
public class BoozerFileController {
    
    @Autowired
    private BoozerFileService boozerFileService;
    
    /**
     * Endpoint to import all Boozer app files into the database
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importBoozerFiles(@RequestBody Map<String, String> request) {
        try {
            String directoryPath = request.get("directoryPath");
            if (directoryPath == null || directoryPath.isEmpty()) {
                directoryPath = "../backend"; // Default to backend directory
            }
            
            int importedCount = boozerFileService.importFilesFromDirectory(directoryPath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Successfully imported " + importedCount + " files");
            response.put("importedCount", importedCount);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to import files: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint to get all Boozer files
     */
    @GetMapping
    public ResponseEntity<List<BoozerFileEntity>> getAllBoozerFiles() {
        return ResponseEntity.ok(boozerFileService.getAllFiles());
    }
    
    /**
     * Endpoint to get a specific Boozer file by path
     */
    @GetMapping("/path/{filePath}")
    public ResponseEntity<?> getBoozerFileByPath(@PathVariable String filePath) {
        // Replace dots with slashes if needed for path encoding
        String decodedPath = filePath.replace("%2F", "/");
        
        Optional<BoozerFileEntity> file = boozerFileService.findByFilePath(decodedPath);
        if (file.isPresent()) {
            return ResponseEntity.ok(file.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "File not found: " + decodedPath);
            return ResponseEntity.status(404).body(response);
        }
    }
    
    /**
     * Endpoint to get a specific Boozer file content by path
     */
    @GetMapping("/content/{filePath}")
    public ResponseEntity<?> getBoozerFileContentByPath(@PathVariable String filePath) {
        // Replace dots with slashes if needed for path encoding
        String decodedPath = filePath.replace("%2F", "/");
        
        Optional<String> content = boozerFileService.getFileContentByPath(decodedPath);
        if (content.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("filePath", decodedPath);
            response.put("content", content.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "File not found: " + decodedPath);
            return ResponseEntity.status(404).body(response);
        }
    }
    
    /**
     * Endpoint to search for Boozer files
     */
    @GetMapping("/search")
    public ResponseEntity<List<BoozerFileEntity>> searchBoozerFiles(@RequestParam String query) {
        return ResponseEntity.ok(boozerFileService.searchFiles(query));
    }
    
    /**
     * Endpoint to get Boozer files by type
     */
    @GetMapping("/type/{fileType}")
    public ResponseEntity<List<BoozerFileEntity>> getBoozerFilesByType(@PathVariable String fileType) {
        return ResponseEntity.ok(boozerFileService.findByFileType(fileType));
    }
    
    /**
     * Endpoint to get Boozer files by package name
     */
    @GetMapping("/package/{packageName}")
    public ResponseEntity<List<BoozerFileEntity>> getBoozerFilesByPackage(@PathVariable String packageName) {
        return ResponseEntity.ok(boozerFileService.findByPackageName(packageName));
    }
    
    /**
     * Endpoint to get Boozer files by class name
     */
    @GetMapping("/class/{className}")
    public ResponseEntity<List<BoozerFileEntity>> getBoozerFilesByClass(@PathVariable String className) {
        return ResponseEntity.ok(boozerFileService.findByClassName(className));
    }
    
    /**
     * Endpoint to get system information about Boozer files
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getBoozerFilesInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("totalFiles", boozerFileService.countFiles());
        response.put("fileTypes", getFileTypeCounts());
        response.put("status", "HEALTHY");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Helper method to get file type counts
     */
    private Map<String, Integer> getFileTypeCounts() {
        Map<String, Integer> typeCounts = new HashMap<>();
        
        List<BoozerFileEntity> allFiles = boozerFileService.getAllFiles();
        for (BoozerFileEntity file : allFiles) {
            String fileType = file.getFileType();
            if (fileType != null) {
                typeCounts.put(fileType, typeCounts.getOrDefault(fileType, 0) + 1);
            }
        }
        
        return typeCounts;
    }
}