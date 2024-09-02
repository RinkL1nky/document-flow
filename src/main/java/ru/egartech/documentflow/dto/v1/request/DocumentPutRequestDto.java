package ru.egartech.documentflow.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocumentPutRequestDto {

    @NotBlank
    @Size(min = 4, max = 128)
    private final String name;

    @NotBlank
    @Size(min = 4, max = 2048)
    private final String comment;

    @Positive
    @Max(32767)
    @NotNull
    @JsonProperty("type_id")
    private final Long typeId;

}
