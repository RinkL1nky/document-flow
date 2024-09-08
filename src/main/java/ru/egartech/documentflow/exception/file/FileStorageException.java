package ru.egartech.documentflow.exception.file;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;
import ru.egartech.documentflow.exceptionhandler.ErrorDetails;

public class FileStorageException extends ApplicationException {

    public FileStorageException(HttpStatus status, String code, String message, ErrorDetails details) {
        super(status, code, message, details);
    }

    public FileStorageException(Throwable exception) {
        super(exception);
    }

}
