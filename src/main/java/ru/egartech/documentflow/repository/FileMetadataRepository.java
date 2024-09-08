package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.entity.FileMetadata;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    /**
     * Обновить срок действия файла.
     * @param fileId ID файла для обновления
     * @param newExpiration временная точка для истечения срока действия файла.
     *                      Если указан null, срок действия файла становится бессрочными.
     */
    @Transactional
    @Modifying
    @Query("UPDATE FileMetadata f SET f.expiresAt = :newExpiration WHERE f.id = :fileId")
    void updateExpiration(Long fileId, LocalDateTime newExpiration);

    /**
     * Удаление всех мета-данных для файлов в указанном бакете, срок действия которых истёк.
     * @param bucket бакет для файлов
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM FileMetadata f WHERE f.expiresAt <= CURRENT_TIMESTAMP AND f.bucket = :bucket")
    void deleteAllExpiredFilesInBucket(String bucket);

    /**
     * Найти все файлы в указанном бакете, срок действия которых истёк.
     * @param bucket бакет для файлов
     * @return Список файлов, срок действия которых истёк
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.expiresAt <= CURRENT_TIMESTAMP AND f.bucket = :bucket")
    List<FileMetadata> findAllExpiredFilesInBucket(String bucket);

}
