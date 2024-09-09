package ru.egartech.documentflow.service.emailtemplateprocessor;

import ru.egartech.documentflow.service.emailclient.EmailMessage;
import ru.egartech.documentflow.entity.EmailTask;

/**
 * Шаблонизатор письма для электронной почты.
 */
public interface MessageTemplateProcessor {

    /**
     * Собрать письмо согласно шаблону и данным из задачи по отправке.
     * @param emailTask задача по отправке
     * @return структура письма для отправки
     */
    EmailMessage process(EmailTask emailTask);

}
