package com.boozer.nexus.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boozer_files")
public class BoozerFileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "package_name")
    private String packageName;
    
    @Column(name = "class_name")
    private String className;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public BoozerFileEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BoozerFileEntity(String filePath, String fileName, String content) {
        this();
        this.filePath = filePath;
        this.fileName = fileName;
        this.content = content;
        this.fileSize = (long) (content != null ? content.length() : 0);
        
        // Extract file type from file name
        if (fileName != null && fileName.contains(".")) {
            this.fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        
        // Extract package and class name for Java files
        if (this.fileType != null && this.fileType.equals("java") && content != null) {
            extractPackageAndClassInfo(content);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.fileSize = (long) (content != null ? content.length() : 0);
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
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
    
    // Helper method to extract package and class information from Java files
    private void extractPackageAndClassInfo(String content) {
        // Extract package name
        if (content.contains("package ")) {
            int packageStart = content.indexOf("package ") + 8;
            int packageEnd = content.indexOf(";", packageStart);
            if (packageEnd > packageStart) {
                this.packageName = content.substring(packageStart, packageEnd).trim();
            }
        }
        
        // Extract class name (simple approach)
        if (content.contains("class ")) {
            int classStart = content.indexOf("class ") + 6;
            int classEnd = content.indexOf("{", classStart);
            if (classEnd == -1) {
                classEnd = content.length();
            }
            String classLine = content.substring(classStart, classEnd).trim();
            if (classLine.contains(" ")) {
                this.className = classLine.substring(0, classLine.indexOf(" ")).trim();
            } else {
                this.className = classLine;
            }
        }
    }
}
