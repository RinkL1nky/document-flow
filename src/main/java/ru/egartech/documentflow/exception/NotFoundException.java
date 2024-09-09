package ru.egartech.documentflow.exception;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exceptionhandler.ErrorDetails;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String field) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", "Object was not found",
                ErrorDetails.builder()
                        .field(field)
                        .build());
    }

}
