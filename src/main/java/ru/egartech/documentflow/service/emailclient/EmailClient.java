package ru.egartech.documentflow.service.emailclient;

import jakarta.mail.MessagingException;

public interface EmailClient {

    void sendMessage(EmailMessage emailMessage) throws MessagingException;

}
