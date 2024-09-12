package ru.egartech.documentflow.exceptionhandler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ErrorDtoWrapper {

    private final List<ErrorDtoItem> errors;

}
