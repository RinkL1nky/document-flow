package ru.egartech.documentflow.exception.auth;

import org.springframework.security.access.AccessDeniedException;

public class ForbiddenException extends AccessDeniedException {

    public ForbiddenException() {
        super("Action cannot be processed due to insufficient permissions");
    }

}
