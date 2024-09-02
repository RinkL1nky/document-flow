package ru.egartech.documentflow.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailTaskRequestDto {

    @JsonProperty("destination_email")
    @NotBlank
    private final String destinationEmail;

    @JsonProperty("template_name")
    @NotBlank
    private final String templateName;

    @NotBlank
    private final String subject;

}
