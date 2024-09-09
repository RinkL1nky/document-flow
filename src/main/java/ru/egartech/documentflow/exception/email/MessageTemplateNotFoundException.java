package ru.egartech.documentflow.exception.email;

import org.springframework.http.HttpStatus;
import ru.egartech.documentflow.exception.ApplicationException;
import ru.egartech.documentflow.exceptionhandler.ErrorDetails;

public class MessageTemplateNotFoundException extends ApplicationException {

    public MessageTemplateNotFoundException() {
        super(HttpStatus.CONFLICT, "MESSAGE_TEMPLATE_NOT_FOUND", "Message template was not found",
                ErrorDetails.builder()
                        .field("templateName")
                        .build());
    }

}
