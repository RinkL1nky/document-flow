package ru.egartech.documentflow.exception.task;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;

public class TaskChainIntegrityViolation extends ApplicationException {

    public TaskChainIntegrityViolation() {
        super(HttpStatus.CONFLICT, "TASK_CHAIN_INTEGRITY_VIOLATION");
    }

}
