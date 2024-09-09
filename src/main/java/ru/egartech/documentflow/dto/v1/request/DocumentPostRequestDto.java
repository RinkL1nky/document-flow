package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocumentPostRequestDto {

    @NotBlank
    @Size(min = 4, max = 128)
    private final String name;

    @NotBlank
    @Size(min = 4, max = 2048)
    private final String comment;

    @Positive
    @Max(32767)
    @NotNull
    private final Long typeId;

    @Positive
    @NotNull
    private final Long fileId;

}
