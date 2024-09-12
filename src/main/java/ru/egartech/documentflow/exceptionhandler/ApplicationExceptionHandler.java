package ru.egartech.documentflow.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ParsingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.egartech.documentflow.exception.ApplicationException;
import ru.egartech.documentflow.exception.email.EmailSendingException;
import ru.egartech.documentflow.exception.file.FileStorageException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorDtoWrapper> handleApplicationException(ApplicationException exception) {
        if(exception.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("Unexpected error has occurred.", exception);
        }
        ErrorDtoItem errorDtoItem = ErrorDtoItem.builder()
                .code(exception.getCode())
                .message(exception.getMessage())
                .details(exception.getErrorDetails())
                .build();

        return new ResponseEntity<>(new ErrorDtoWrapper(List.of(errorDtoItem)), exception.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDtoWrapper handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ErrorDtoItem> errorDtoItemList = new ArrayList<>();
        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String prefix = (fieldError.isBindingFailure()) ? "BINDING_" : "VALIDATION_";
            String code = Optional.ofNullable(fieldError.getCode())
                    .map(string -> ParsingUtils.reconcatenateCamelCase(string, "_") + "_ERROR")
                    .orElse(prefix + "_ERROR")
                    .toUpperCase();
            errorDtoItemList.add(ErrorDtoItem.builder()
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

        return new ErrorDtoWrapper(errorDtoItemList);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorDtoWrapper> handleEmailSendingException(EmailSendingException exception) {
        log.error("Failed to send email message.", exception);
        ErrorDtoItem errorDtoItem = ErrorDtoItem.builder()
                .code(exception.getCode())
                .message("Failed to send email message.")
                .details(exception.getErrorDetails())
                .build();

        return new ResponseEntity<>(new ErrorDtoWrapper(List.of(errorDtoItem)), exception.getStatus());
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorDtoWrapper> handleFileStorageException(FileStorageException exception) {
        if(exception.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("Unexpected error has occurred.", exception);
        }
        ErrorDtoItem errorDtoItem = ErrorDtoItem.builder()
                .code(exception.getCode())
                .details(exception.getErrorDetails())
                .build();

        return new ResponseEntity<>(new ErrorDtoWrapper(List.of(errorDtoItem)), exception.getStatus());
    }

}
