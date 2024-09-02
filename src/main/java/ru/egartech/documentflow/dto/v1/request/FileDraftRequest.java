package ru.egartech.documentflow.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileDraftRequest {

    @Size(min = 4, max = 255)
    @NotBlank
    private final String filename;

    @JsonProperty("content_type")
    @Size(min = 4, max = 255)
    @NotBlank
    private final String contentType;

}
