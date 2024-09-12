package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.FileDraftRequest;
import ru.egartech.documentflow.dto.v1.request.FileMetadataRequestDto;
import ru.egartech.documentflow.dto.v1.response.FileDownloadResponse;
import ru.egartech.documentflow.dto.v1.response.FileUploadResponse;
import ru.egartech.documentflow.service.v1.SimpleStorageService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
public class FileStorageController {
    private final SimpleStorageService simpleStorageService;

    @GetMapping("/file/{fileId}")
    public FileDownloadResponse getFile(@PathVariable @Positive Long fileId) {
        return simpleStorageService.getDownloadUrl(fileId);
    }

    @PostMapping("/file/draft")
    @ResponseStatus(HttpStatus.CREATED)
    public FileUploadResponse createDraft(@RequestBody @Valid FileDraftRequest draftRequest) {
        return simpleStorageService.createDraft(draftRequest);
    }

    @PutMapping("/file/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFileMetadata(@PathVariable @Positive Long fileId,
                                   @RequestBody @Valid FileMetadataRequestDto fileMetadataRequestDto) {
        simpleStorageService.updateFileMetadata(fileId, fileMetadataRequestDto);
    }

}
