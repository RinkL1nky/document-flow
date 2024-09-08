package ru.egartech.documentflow.service.emailclient;

import jakarta.mail.MessagingException;

public interface EmailClient {

    /**
     * Отправить письмо согласно данным из его собранной структуры.
     * @param emailMessage структура письма для отправки
     * @throws MessagingException если письмо отправить не удалось
     */
    void sendMessage(EmailMessage emailMessage) throws MessagingException;

}
