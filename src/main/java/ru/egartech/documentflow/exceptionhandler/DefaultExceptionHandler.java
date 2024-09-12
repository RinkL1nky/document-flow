package ru.egartech.documentflow.exceptionhandler;

import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Override
    protected final ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                                HttpStatusCode statusCode, WebRequest request) {
        List<ErrorDtoItem> errorDtoItemList = new ArrayList<>();
        if(body instanceof ProblemDetail problemDetail) {
            String code = Optional.ofNullable(problemDetail.getTitle())
                    .map(s -> s.replace(' ', '_').toUpperCase())
                    .orElse(null);
            errorDtoItemList.add(ErrorDtoItem.builder()
                    .code(code)
                    .message(problemDetail.getDetail())
                    .build()
            );
        }

        return new ResponseEntity<>(new ErrorDtoWrapper(errorDtoItemList), statusCode);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public ErrorDtoWrapper handleAccessDeniedException(AccessDeniedException exception) {
        ErrorDtoItem errorDtoItem = ErrorDtoItem.builder()
                .code("FORBIDDEN")
                .message(exception.getMessage())
                .build();

        return new ErrorDtoWrapper(List.of(errorDtoItem));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDtoWrapper handleException(Exception exception) {
        logger.warn("Unexpected error has occurred.", exception);
        ErrorDtoItem errorDtoItem = ErrorDtoItem.builder()
                .code("UNEXPECTED_ERROR")
                .message("Unexpected error has occurred. Please try again later.")
                .build();

        return new ErrorDtoWrapper(List.of(errorDtoItem));
    }

}
