package ru.egartech.documentflow.exception.task;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class WrongTaskTypeException extends ApplicationException {

    public WrongTaskTypeException() {
        super(HttpStatus.CONFLICT, "WRONG_TASK_TYPE", "Action cannot be be processed due to task type");
    }

}
