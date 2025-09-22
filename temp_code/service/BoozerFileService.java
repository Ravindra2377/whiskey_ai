package com.boozer.whiskey.service;

import com.boozer.whiskey.model.BoozerFileEntity;
import com.boozer.whiskey.repository.BoozerFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BoozerFileService {
    
    @Autowired
    private BoozerFileRepository fileRepository;
    
    public BoozerFileEntity saveFile(BoozerFileEntity file) {
        return fileRepository.save(file);
    }
    
    public Optional<BoozerFileEntity> findByFilePath(String filePath) {
        return fileRepository.findByFilePath(filePath);
    }
    
    public Optional<BoozerFileEntity> findByFileName(String fileName) {
        return fileRepository.findByFileName(fileName);
    }
    
    public List<BoozerFileEntity> findByFileType(String fileType) {
        return fileRepository.findByFileType(fileType);
    }
    
    public List<BoozerFileEntity> findByPackageName(String packageName) {
        return fileRepository.findByPackageName(packageName);
    }
    
    public List<BoozerFileEntity> findByClassName(String className) {
        return fileRepository.findByClassName(className);
    }
    
    public List<BoozerFileEntity> searchFiles(String query) {
        // Search by file name, package name, or class name
        return fileRepository.findAll().stream()
                .filter(file -> 
                    (file.getFileName() != null && file.getFileName().toLowerCase().contains(query.toLowerCase())) ||
                    (file.getPackageName() != null && file.getPackageName().toLowerCase().contains(query.toLowerCase())) ||
                    (file.getClassName() != null && file.getClassName().toLowerCase().contains(query.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
    
    public List<BoozerFileEntity> getAllFiles() {
        return fileRepository.findAll();
    }
    
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
    
    public void deleteFileByPath(String filePath) {
        fileRepository.deleteByFilePath(filePath);
    }
    
    public long countFiles() {
        return fileRepository.count();
    }
    
    public boolean existsByFilePath(String filePath) {
        return fileRepository.existsByFilePath(filePath);
    }
    
    // Method to scan and import all files from a directory
    public int importFilesFromDirectory(String directoryPath) throws IOException {
        Path rootPath = Paths.get(directoryPath);
        if (!Files.exists(rootPath)) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        
        int importedCount = 0;
        
        try (Stream<Path> paths = Files.walk(rootPath)) {
            List<Path> files = paths.filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        // Filter for common source code files
                        return fileName.endsWith(".java") || 
                               fileName.endsWith(".js") || 
                               fileName.endsWith(".py") || 
                               fileName.endsWith(".xml") || 
                               fileName.endsWith(".properties") || 
                               fileName.endsWith(".sql") ||
                               fileName.endsWith(".md") ||
                               fileName.endsWith(".json");
                    })
                    .collect(Collectors.toList());
            
            for (Path filePath : files) {
                String relativePath = rootPath.relativize(filePath).toString().replace("\\", "/");
                // Check if file already exists
                if (!existsByFilePath(relativePath)) {
                    try {
                        String content = Files.readString(filePath);
                        String fileName = filePath.getFileName().toString();
                        
                        BoozerFileEntity fileEntity = new BoozerFileEntity(relativePath, fileName, content);
                        saveFile(fileEntity);
                        importedCount++;
                    } catch (Exception e) {
                        // Log error but continue with other files
                        System.err.println("Error importing file " + filePath + ": " + e.getMessage());
                    }
                }
            }
        }
        
        return importedCount;
    }
    
    // Method to get file content by path
    public Optional<String> getFileContentByPath(String filePath) {
        Optional<BoozerFileEntity> fileEntity = findByFilePath(filePath);
        return fileEntity.map(BoozerFileEntity::getContent);
    }
}