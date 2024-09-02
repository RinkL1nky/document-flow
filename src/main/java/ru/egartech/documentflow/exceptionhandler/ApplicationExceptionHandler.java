package ru.egartech.documentflow.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.egartech.documentflow.exception.ApplicationException;
import ru.egartech.documentflow.exception.email.EmailSendingException;
import ru.egartech.documentflow.exception.file.FileStorageException;
import ru.egartech.documentflow.responsewrapper.ResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseWrapper<Void> handleApplicationException(ApplicationException exception) {
        ErrorDto errorDto = ErrorDto.builder()
                .code(exception.getCode())
                .details(exception.getErrorDetails())
                .build();
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(exception.getStatus().value())
                .errors(List.of(errorDto))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseWrapper<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String prefix = (fieldError.isBindingFailure()) ? "BINDING_" : "VALIDATION_";
            String code = Optional.ofNullable(fieldError.getCode())
                    .map(string -> ParsingUtils.reconcatenateCamelCase(string, "_") + "_ERROR")
                    .orElse(prefix + "_ERROR")
                    .toUpperCase();
            errorDtoList.add(ErrorDto.builder()
                    .code(prefix + code)
                    .message(Optional.ofNullable(fieldError.getDefaultMessage())
                            .map(StringUtils::capitalize)
                            .orElse(null)
                    )
                    .details(ErrorDetails.builder()
                            .field(ParsingUtils.reconcatenateCamelCase(fieldError.getField(), "_"))
                            .build()
                    )
                    .build()
            );
        }
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(exception.getStatusCode().value())
                .errors(errorDtoList)
                .build();
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseWrapper<Void> handleEmailSendingException(EmailSendingException exception) {
        log.error("Failed to send email message.", exception);
        ErrorDto errorDto = ErrorDto.builder()
                .code(exception.getCode())
                .details(exception.getErrorDetails())
                .build();
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(exception.getStatus().value())
                .errors(List.of(errorDto))
                .build();
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseWrapper<Void> handleFileStorageException(FileStorageException exception) {
        log.error("FileStorageException has occurred.", exception);
        ErrorDto errorDto = ErrorDto.builder()
                .code(exception.getCode())
                .details(exception.getErrorDetails())
                .build();
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(exception.getStatus().value())
                .errors(List.of(errorDto))
                .build();
    }



}
