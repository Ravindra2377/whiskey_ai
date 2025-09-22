package com.boozer.nexus.repository;

import com.boozer.nexus.model.BoozerFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoozerFileRepository extends JpaRepository<BoozerFileEntity, Long> {
    
    Optional<BoozerFileEntity> findByFilePath(String filePath);
    
    Optional<BoozerFileEntity> findByFileName(String fileName);
    
    List<BoozerFileEntity> findByFileType(String fileType);
    
    List<BoozerFileEntity> findByPackageName(String packageName);
    
    List<BoozerFileEntity> findByClassName(String className);
    
    List<BoozerFileEntity> findByFileNameContaining(String fileName);
    
    List<BoozerFileEntity> findByPackageNameContaining(String packageName);
    
    List<BoozerFileEntity> findByClassNameContaining(String className);
    
    List<BoozerFileEntity> findByFileTypeAndPackageName(String fileType, String packageName);
    
    boolean existsByFilePath(String filePath);
    
    void deleteByFilePath(String filePath);
}
