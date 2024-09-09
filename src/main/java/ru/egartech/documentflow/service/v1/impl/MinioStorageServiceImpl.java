package ru.egartech.documentflow.service.v1.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import ru.egartech.documentflow.dto.v1.request.FileDraftRequest;
import ru.egartech.documentflow.dto.v1.request.FileMetadataRequestDto;
import ru.egartech.documentflow.dto.v1.response.FileDownloadResponse;
import ru.egartech.documentflow.dto.v1.response.FileUploadResponse;
import ru.egartech.documentflow.entity.FileMetadata;
import ru.egartech.documentflow.exception.auth.ForbiddenException;
import ru.egartech.documentflow.exception.file.FileIsExpiredException;
import ru.egartech.documentflow.exception.file.FileStorageException;
import ru.egartech.documentflow.exception.NotFoundException;
import ru.egartech.documentflow.properties.S3Properties;
import ru.egartech.documentflow.repository.FileMetadataRepository;
import ru.egartech.documentflow.service.v1.SimpleStorageService;
import ru.egartech.documentflow.util.AuthenticationFacade;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioStorageServiceImpl implements SimpleStorageService {
    private final MinioClient minioClient;
    private final S3Properties s3Properties;
    private final FileMetadataRepository fileMetaRepository;
    private final AuthenticationFacade authenticationFacade;

    @Cacheable("fileDownloadResponse")
    @Override
    public FileDownloadResponse getDownloadUrl(Long fileId) {
        FileMetadata fileMeta = fileMetaRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("fileId"));
        if(fileMeta.isExpired()) {
            throw new FileIsExpiredException();
        }

        try {
            Map<String, String> extraParams = new HashMap<>();
            extraParams.put("response-content-type", fileMeta.getContentType());
            extraParams.put("response-content-disposition", "attachment; name=" + fileMeta.getFilename());

            String downloadUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(fileMeta.getBucket())
                    .object(fileMeta.getPath())
                    .expiry(s3Properties.getDownloadExpirationMinutes(), TimeUnit.MINUTES)
                    .extraQueryParams(extraParams)
                    .build()
            );
            return FileDownloadResponse.builder()
                    .id(fileId)
                    .downloadUrl(downloadUrl)
                    .build();

        } catch (Exception exception) {
            throw new FileStorageException(exception);
        }
    }

    @Override
    public FileUploadResponse getUploadFormData(Long fileId) {
        FileMetadata file = fileMetaRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("fileId"));
        return getUploadFormData(file);
    }

    @Override
    public FileUploadResponse getUploadFormData(FileMetadata file) {
        if(file.isExpired()) {
            throw new FileIsExpiredException();
        }

        PostPolicy policy = new PostPolicy(file.getBucket(), ZonedDateTime.now()
                .plusMinutes(s3Properties.getUploadExpirationMinutes())
        );
        policy.addEqualsCondition("key", file.getPath());
        policy.addStartsWithCondition("Content-Type", file.getContentType());
        policy.addContentLengthRangeCondition(s3Properties.getMinContentLength(), s3Properties.getMaxContentLength());

        try {
            Map<String, String> formData = new LinkedHashMap<>(minioClient.getPresignedPostFormData(policy));

            formData.put("key", file.getPath());
            formData.put("Content-Type", file.getContentType());
            formData.put("file", null); // according to amazon s3, keys following the file will be ignored

            return FileUploadResponse.builder()
                    .id(file.getId())
                    .uploadUrl(UriComponentsBuilder.fromHttpUrl(s3Properties.getEndpoint())
                            .path(file.getBucket())
                            .toUriString()
                    )
                    .formData(formData)
                    .build();
        } catch (Exception exception) {
            throw new FileStorageException(exception);
        }
    }

    @Override
    public FileUploadResponse createDraft(FileDraftRequest draftRequest) {
        FileMetadata fileMeta = new FileMetadata();
        fileMeta.setBucket(s3Properties.getDraftBucket());
        fileMeta.setPath(String.format("%s/%s", authenticationFacade.getCurrentEmployee().getId(), UUID.randomUUID()));
        fileMeta.setContentType(draftRequest.getContentType());
        fileMeta.setFilename(draftRequest.getFilename());
        fileMeta.setExpiresAt(LocalDateTime.now().plusDays(s3Properties.getDraftExpirationDays()));
        fileMeta = fileMetaRepository.save(fileMeta);

        return getUploadFormData(fileMeta);
    }

    @Override
    public FileMetadata applyDraft(Long draftFileId) {
        FileMetadata draftMeta = fileMetaRepository.findById(draftFileId)
                .orElseThrow(() -> new NotFoundException("fileId"));
        if(draftMeta.isExpired()) {
            throw new FileIsExpiredException();
        }

        FileMetadata newFileMeta = new FileMetadata();
        newFileMeta.setBucket(s3Properties.getCommonBucket());
        newFileMeta.setPath(String.format("%s/%s", draftMeta.getUploader().getId(), UUID.randomUUID()));
        newFileMeta.setContentType(draftMeta.getContentType());
        newFileMeta.setFilename(draftMeta.getFilename());
        newFileMeta = fileMetaRepository.save(newFileMeta);

        moveFile(draftMeta, newFileMeta);

        return newFileMeta;
    }

    @Override
    public void moveFile(Long sourceFileId, Long destinationFileId) {
        FileMetadata sourceFileMeta = fileMetaRepository.findById(sourceFileId)
                .orElseThrow(() -> new NotFoundException("sourceFileId"));
        FileMetadata destinationFileMeta = fileMetaRepository.findById(destinationFileId)
                .orElseThrow(() -> new NotFoundException("destinationFileId"));
        moveFile(sourceFileMeta, destinationFileMeta);
    }

    @Override
    public void moveFile(FileMetadata sourceFile, FileMetadata destinationFile) {
        if(sourceFile.isExpired() || destinationFile.isExpired()) {
            throw new FileIsExpiredException();
        }
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder()
                            .bucket(sourceFile.getBucket())
                            .object(sourceFile.getPath())
                            .build()
                    )
                    .bucket(destinationFile.getBucket())
                    .object(destinationFile.getPath())
                    .build()
            );

        } catch (Exception exception) {
            throw new FileStorageException(exception);
        }
    }

    @CacheEvict(value = "fileDownloadResponse", key = "#fileId")
    @Transactional
    @Override
    public void updateFileMetadata(Long fileId, FileMetadataRequestDto fileMetadataRequestDto) {
        FileMetadata fileMetadata = fileMetaRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("fileId"));

        if(!authenticationFacade.isCurrentEmployee(fileMetadata.getUploader())) {
            throw new ForbiddenException();
        }

        fileMetadata.setFilename(fileMetadataRequestDto.getFilename());
        fileMetadata.setContentType(fileMetadataRequestDto.getContentType());
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMetaRepository.findById(fileId).ifPresent(this::deleteFile);
    }

    @CacheEvict(value = "fileDownloadResponse", key = "#file.id")
    @Override
    public void deleteFile(FileMetadata file) {
        if(!authenticationFacade.isCurrentEmployee(file.getUploader())) {
            throw new ForbiddenException();
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(file.getBucket())
                    .object(file.getPath())
                    .build()
            );
        } catch (Exception exception) {
            throw new FileStorageException(exception);
        }
        fileMetaRepository.delete(file);
    }

    public InputStream getFileStream(FileMetadata file) {
        if(file.isExpired()) {
            throw new FileIsExpiredException();
        }
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(file.getBucket())
                    .object(file.getPath())
                    .build()
            );
        } catch (Exception exception) {
            throw new FileStorageException(exception);
        }
    }

    public void setFileExpiration(Long fileId, LocalDateTime newExpiration) {
        fileMetaRepository.updateExpiration(fileId, newExpiration);
    }

    public void setFileExpired(Long fileId) {
        setFileExpiration(fileId, LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void deleteExpiredFiles() {
        log.info("Scheduled file clean up is started.");
        fileMetaRepository.deleteAllExpiredFilesInBucket(s3Properties.getDraftBucket());
        List<FileMetadata> commonBucketFiles =
                fileMetaRepository.findAllExpiredFilesInBucket(s3Properties.getCommonBucket());

        try {
            Iterable<Result<DeleteError>> deleteResults = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(s3Properties.getCommonBucket())
                    .objects(commonBucketFiles.stream().map(file -> new DeleteObject(file.getPath())).toList())
                    .build());
            for(Result<DeleteError> result : deleteResults) {
                DeleteError error = result.get();
                log.error("Failed to delete file {} in bucket {}: {}",
                        error.objectName(), error.bucketName(), error.message());
                commonBucketFiles.removeIf(file -> file.getPath().equals(error.objectName()));
            }
        } catch (Exception exception) {
            log.info("Unexpected exception has occurred during scheduled file clean up for bucket {}",
                    s3Properties.getCommonBucket());
            throw new FileStorageException(exception);
        }

        fileMetaRepository.deleteAllById(commonBucketFiles.stream().map(FileMetadata::getId).toList());

        log.info("Scheduled file clean up is finished.");
    }

}
