package ru.egartech.documentflow.service.v1;

import ru.egartech.documentflow.dto.v1.request.FileDraftRequest;
import ru.egartech.documentflow.dto.v1.request.FileMetadataRequestDto;
import ru.egartech.documentflow.dto.v1.response.FileDownloadResponse;
import ru.egartech.documentflow.dto.v1.response.FileUploadResponse;
import ru.egartech.documentflow.entity.FileMetadata;

import java.io.InputStream;
import java.time.LocalDateTime;

public interface SimpleStorageService {

    FileDownloadResponse getDownloadUrl(Long fileId);

    FileUploadResponse getUploadFormData(Long fileId);

    FileUploadResponse getUploadFormData(FileMetadata file);

    FileUploadResponse createDraft(FileDraftRequest draftRequest);

    FileMetadata applyDraft(Long draftFileId);

    void moveFile(Long sourceFileId, Long destinationFileId);

    void moveFile(FileMetadata sourceFile, FileMetadata destinationFile);

    void updateFileMetadata(Long fileId, FileMetadataRequestDto fileMetadataRequestDto);

    void deleteFile(Long fileId);

    void deleteFile(FileMetadata file);

    InputStream getFileStream(FileMetadata file);

    void setFileExpiration(Long fileId, LocalDateTime newExpiration);

    void setFileExpired(Long fileId);

}
