package ru.egartech.documentflow.exceptionhandler;

import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.egartech.documentflow.responsewrapper.ResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Override
    protected final ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                                HttpStatusCode statusCode, WebRequest request) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        if(body instanceof ProblemDetail problemDetail) {
            String code = Optional.ofNullable(problemDetail.getTitle())
                    .map(s -> s.replace(' ', '_').toUpperCase())
                    .orElse(null);
            errorDtoList.add(ErrorDto.builder()
                    .code(code)
                    .message(problemDetail.getDetail())
                    .build()
            );
        }
        ResponseWrapper<?> responseWrapper = ResponseWrapper.builder()
                .success(false)
                .status(statusCode.value())
                .errors(errorDtoList)
                .build();
        return new ResponseEntity<>(responseWrapper, new HttpHeaders(), 200);
    }

    @ExceptionHandler(Exception.class)
    public ResponseWrapper<Void> handleException(Exception exception) {
        logger.warn("Unexpected error has occurred.", exception);
        ErrorDto errorDto = ErrorDto.builder()
                .code("UNEXPECTED_ERROR")
                .message("Unexpected error has occurred. Please try again later.")
                .build();
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(500)
                .errors(List.of(errorDto))
                .build();
    }

}
