package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class StoredFileMetaResponseDto {

    private Long id;

    private String name;

    private String contentType;

    private Long size;

    private Instant uploadedAt;

}
