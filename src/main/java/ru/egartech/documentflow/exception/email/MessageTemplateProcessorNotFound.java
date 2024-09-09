package ru.egartech.documentflow.exception.email;

import ru.egartech.documentflow.exception.ApplicationException;

public class MessageTemplateProcessorNotFound extends ApplicationException {

    public MessageTemplateProcessorNotFound(String templateName) {
        super(String.format("Message processor for template %s was not found.", templateName));
    }

}
