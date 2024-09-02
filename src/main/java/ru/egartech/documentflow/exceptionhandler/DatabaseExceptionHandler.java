package ru.egartech.documentflow.exceptionhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.egartech.documentflow.responsewrapper.ResponseWrapper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class DatabaseExceptionHandler {

    private final JpaProperties jpaProperties;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseWrapper<Void> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        if(exception.getCause() instanceof ConstraintViolationException constraintException) {
            Optional<ConstraintErrorCode> errorCode =
                    ConstraintErrorCode.findOne(constraintException.getSQLState(), jpaProperties.getDatabase());
            errorCode.ifPresent(errorEnum -> errorDtoList.add(ErrorDto.builder()
                    .code(errorEnum.getErrorCode())
                    .message(MessageFormat.format(errorEnum.getMessage(), constraintException.getConstraintName()))
                    .details(ErrorDetails.builder()
                            .field(constraintException.getConstraintName())
                            .build()
                    )
                    .build()
            ));
        }
        if(errorDtoList.isEmpty()) {
            log.warn("Data cannot be committed to database:", exception);
            errorDtoList.add(ErrorDto.builder()
                    .code("DATABASE_INTEGRITY_VIOLATION")
                    .message("Incorrect data cannot be committed to database")
                    .build()
            );
        }
        return ResponseWrapper.<Void>builder()
                .success(false)
                .status(409)
                .errors(errorDtoList)
                .build();
    }

}
