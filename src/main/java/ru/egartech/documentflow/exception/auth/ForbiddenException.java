package ru.egartech.documentflow.exception.auth;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

}
