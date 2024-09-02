package ru.egartech.documentflow.dto.v1.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailTaskResponseDto {

    @NotNull
    @Positive
    private final Long taskId;

    @NotBlank
    private final String destinationEmail;

    @NotBlank
    private final String templateName;

    @NotBlank
    private final String subject;

}
