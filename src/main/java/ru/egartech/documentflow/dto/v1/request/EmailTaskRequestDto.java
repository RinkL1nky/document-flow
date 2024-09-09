package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailTaskRequestDto {

    @NotBlank
    private final String destinationEmail;

    @NotBlank
    private final String templateName;

    @NotBlank
    private final String subject;

}
