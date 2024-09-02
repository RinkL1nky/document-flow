package ru.egartech.documentflow.exception.email;

import ru.egartech.documentflow.exception.ApplicationException;

public class EmailSendingException extends ApplicationException {

    public EmailSendingException(Throwable throwable) {
        super(throwable);
    }

}
