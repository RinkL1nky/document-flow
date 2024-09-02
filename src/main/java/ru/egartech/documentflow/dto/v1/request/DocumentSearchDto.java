package ru.egartech.documentflow.dto.v1.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.beans.ConstructorProperties;
import java.time.Instant;

@Data
public class DocumentSearchDto {

    @ConstructorProperties({"name", "comment", "type_id", "issuer_id", "before_created_at", "after_created_at"})
    public DocumentSearchDto(String name, String comment, Long typeId, Long issuerId, Instant beforeCreatedAt, Instant afterCreatedAt) {
        this.name = name;
        this.comment = comment;
        this.typeId = typeId;
        this.issuerId = issuerId;
        this.beforeCreatedAt = beforeCreatedAt;
        this.afterCreatedAt = afterCreatedAt;
    }

    private final String name;

    private final String comment;

    private final Long typeId;

    private final Long issuerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant beforeCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant afterCreatedAt;

}
