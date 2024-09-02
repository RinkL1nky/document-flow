package ru.egartech.documentflow.dto.v1.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.egartech.documentflow.entity.Task;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class TaskSearchDto {

    @ConstructorProperties({"name", "comment", "document_id", "from_deadline", "to_deadline", "appointee_id",
                            "type", "status", "issuer_id", "from_created_at", "to_created_at"})
    public TaskSearchDto(String name, String comment, Long documentId, LocalDateTime fromDeadline,
                         LocalDateTime toDeadline, Long appointeeId, Task.Type type, Task.Status status,
                         Long issuerId, Instant fromCreatedAt, Instant toCreatedAt) {
        this.name = name;
        this.comment = comment;
        this.documentId = documentId;
        this.fromDeadline = fromDeadline;
        this.toDeadline = toDeadline;
        this.appointeeId = appointeeId;
        this.type = type;
        this.status = status;
        this.issuerId = issuerId;
        this.fromCreatedAt = fromCreatedAt;
        this.toCreatedAt = toCreatedAt;
    }

    private final String name;

    private final String comment;

    private final Long documentId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDateTime fromDeadline;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDateTime toDeadline;

    private final Long appointeeId;

    private final Task.Type type;

    private final Task.Status status;

    private final Long issuerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant fromCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant toCreatedAt;

}
