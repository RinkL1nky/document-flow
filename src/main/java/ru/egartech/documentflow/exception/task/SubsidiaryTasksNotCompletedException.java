package ru.egartech.documentflow.exception.task;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class SubsidiaryTasksNotCompletedException extends ApplicationException {

    public SubsidiaryTasksNotCompletedException() {
        super(HttpStatus.CONFLICT, "SUBSIDIARY_TASKS_NOT_COMPLETED");
    }

}
