package ru.egartech.documentflow.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.egartech.documentflow.entity.Task;
import ru.egartech.documentflow.validation.constraints.EnumString;

import java.time.LocalDateTime;

@Builder
@Data
public class TaskPostRequestDto {

    @NotBlank
    @Size(min = 4, max = 128)
    private final String name;

    @NotBlank
    @Size(min = 4, max = 128)
    private final String comment;

    @JsonProperty("document_id")
    @NotNull
    @Positive
    private final Long documentId;

    @NotNull
    @Future
    private final LocalDateTime deadline;

    @JsonProperty("parent_id")
    @Positive
    private final Long parentId;

    @JsonProperty("appointee_id")
    @NotNull
    @Positive
    private final Long appointeeId;

    @NotNull
    @EnumString(enumClass = Task.Type.class, message = "Provided enum was not found")
    private final String type;

}
