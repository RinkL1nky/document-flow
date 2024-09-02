package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
public class TaskResponseDto {

    private final Long id;

    private final String name;

    private final String comment;

    private final Long documentId;

    private final LocalDateTime deadline;

    private final Long parentId;

    private final Long appointeeId;

    private final String type;

    private final String status;

    private final Long issuerId;

    private final Instant createdAt;

}
