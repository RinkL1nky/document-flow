package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class DocumentPutRequestDto {

    @NotBlank
    @Size(min = 4, max = 128)
    private final String name;

    @NotBlank
    @Size(min = 4, max = 2048)
    private final String comment;

    @Range(min = 1, max = 32767)
    @NotNull
    private final Long typeId;

}
