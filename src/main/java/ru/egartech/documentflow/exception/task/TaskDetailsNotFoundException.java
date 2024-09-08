package ru.egartech.documentflow.exception.task;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class TaskDetailsNotFoundException extends ApplicationException {

    public TaskDetailsNotFoundException() {
        super(HttpStatus.CONFLICT, "TASK_DETAILS_NOT_FOUND", "Task details are missing");
    }

}
