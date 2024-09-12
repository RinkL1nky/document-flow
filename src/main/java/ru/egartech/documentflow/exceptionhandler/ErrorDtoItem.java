package ru.egartech.documentflow.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class ErrorDtoItem {

    private final String code;

    private final String message;

    private final ErrorDetails details;

}
