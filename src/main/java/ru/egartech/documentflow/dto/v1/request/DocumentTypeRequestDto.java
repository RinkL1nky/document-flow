package ru.egartech.documentflow.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
public class DocumentTypeRequestDto {

    @JsonCreator
    public DocumentTypeRequestDto(String name) {
        this.name = name;
    }

    @NotBlank
    @Size(min = 4, max = 128)
    private final String name;

}
