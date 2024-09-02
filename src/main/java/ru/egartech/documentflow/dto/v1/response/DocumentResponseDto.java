package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@Data
public class DocumentResponseDto {

    private Long id;

    private String name;

    private String comment;

    private Long typeId;

    private Long fileId;

    private Long parentId;

    private List<Long> signatoryIds;

    private Long issuerId;

    private Instant createdAt;

}
