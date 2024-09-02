package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.entity.FileMetadata;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE FileMetadata f SET f.expiresAt = :newExpiration WHERE f.id = :fileId")
    void updateExpiration(Long fileId, LocalDateTime newExpiration);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileMetadata f WHERE f.expiresAt <= CURRENT_TIMESTAMP AND f.bucket = :bucket")
    void deleteAllExpiredFilesInBucket(String bucket);

    @Query("SELECT f FROM FileMetadata f WHERE f.expiresAt <= CURRENT_TIMESTAMP AND f.bucket = :bucket")
    List<FileMetadata> findAllExpiredFilesInBucket(String bucket);

}
