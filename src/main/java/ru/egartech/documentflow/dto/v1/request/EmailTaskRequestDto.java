package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Email(regexp = "^[a-z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-z0-9.-]+$", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private final String subject;

}
