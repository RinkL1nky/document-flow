package ru.egartech.documentflow.dto.v1.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmployeeRequestDto {

    @NotBlank
    @Size(min = 3, max = 32)
    private final String username;

    @NotBlank
    @Size(min = 8, max = 255)
    private final String password;

    @NotBlank
    @Size(min = 3, max = 32)
    private final String firstName;

    @NotBlank
    @Size(min = 3, max = 32)
    private final String lastName;

    @NotBlank
    @Email(regexp = "^[a-z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-z0-9.-]+$", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private final String email;

}
