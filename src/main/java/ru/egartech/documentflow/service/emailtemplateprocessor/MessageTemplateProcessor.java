package ru.egartech.documentflow.service.emailtemplateprocessor;

import ru.egartech.documentflow.service.emailclient.EmailMessage;
import ru.egartech.documentflow.entity.EmailTask;

public interface MessageTemplateProcessor {

    EmailMessage process(EmailTask emailTask);

}
