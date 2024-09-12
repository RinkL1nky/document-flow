package ru.egartech.documentflow.exceptionhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class DatabaseExceptionHandler {

    private final JpaProperties jpaProperties;

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDtoWrapper handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        List<ErrorDtoItem> errorDtoItemList = new ArrayList<>();
        if(exception.getCause() instanceof ConstraintViolationException constraintException) {
            Optional<ConstraintErrorCode> errorCode =
                    ConstraintErrorCode.findOne(constraintException.getSQLState(), jpaProperties.getDatabase());
            errorCode.ifPresent(errorEnum -> errorDtoItemList.add(ErrorDtoItem.builder()
                    .code(errorEnum.getErrorCode())
                    .message(MessageFormat.format(errorEnum.getMessage(), constraintException.getConstraintName()))
                    .details(ErrorDetails.builder()
                            .field(constraintException.getConstraintName())
                            .build()
                    )
                    .build()
            ));
        }
        if(errorDtoItemList.isEmpty()) {
            log.warn("Data cannot be committed to database:", exception);
            errorDtoItemList.add(ErrorDtoItem.builder()
                    .code("DATABASE_INTEGRITY_VIOLATION")
                    .message("Incorrect data cannot be committed to database")
                    .build()
            );
        }

        return new ErrorDtoWrapper(errorDtoItemList);
    }

}
