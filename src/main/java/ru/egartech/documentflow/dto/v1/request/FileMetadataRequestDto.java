package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileMetadataRequestDto {

    @Size(min = 4, max = 255)
    @NotBlank
    private final String filename;

    @Size(min = 4, max = 255)
    @NotBlank
    private final String contentType;

}
