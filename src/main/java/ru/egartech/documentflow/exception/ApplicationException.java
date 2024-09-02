package ru.egartech.documentflow.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exceptionhandler.ErrorDetails;

@Getter
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final ErrorDetails errorDetails;

    public ApplicationException(HttpStatus status, String code, ErrorDetails errorDetails) {
        this.status = status;
        this.code = code;
        this.errorDetails = errorDetails;
    }

    public ApplicationException(HttpStatus status, String code, String message, ErrorDetails errorDetails) {
        super(message);
        this.status = status;
        this.code = code;
        this.errorDetails = errorDetails;
    }

    public ApplicationException(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
        this.errorDetails = null;
    }

    public ApplicationException(Throwable exception) {
        super(exception);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = "UNEXPECTED_ERROR";
        this.errorDetails = null;
    }

    public ApplicationException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = "UNEXPECTED_ERROR";
        this.errorDetails = null;
    }

}
