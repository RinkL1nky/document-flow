package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocumentTypeResponseDto {

    private Long id;

    private String name;

}
