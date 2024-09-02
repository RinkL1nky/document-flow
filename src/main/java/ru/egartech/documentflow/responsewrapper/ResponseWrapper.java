package ru.egartech.documentflow.responsewrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.egartech.documentflow.exceptionhandler.ErrorDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class ResponseWrapper<T> {

    private final Boolean success;

    private final Integer status;

    private final T body;

    private final List<ErrorDto> errors;

}
