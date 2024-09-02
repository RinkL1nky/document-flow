package ru.egartech.documentflow.exception.task;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class WrongTaskStatusException extends ApplicationException {

    public WrongTaskStatusException() {
        super(HttpStatus.CONFLICT, "WRONG_TASK_STATUS");
    }

}
