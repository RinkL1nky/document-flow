package ru.egartech.documentflow.service.emailclient;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.egartech.documentflow.properties.EmailClientProperties;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Service
public class DeveloperEmailClient implements EmailClient {

    private final JavaMailSender emailSender;
    private final EmailClientProperties emailClientProperties;

    @Override
    public void sendMessage(EmailMessage emailMessage) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setFrom(emailClientProperties.getSenderAddress());
        messageHelper.setTo(emailMessage.getRecipient());
        messageHelper.setSubject(emailMessage.getSubject());
        messageHelper.setText(emailMessage.getBody(), emailMessage.isHtml());

        for (var entry : emailMessage.getAttachments().entrySet()) {
            messageHelper.addAttachment(entry.getKey(), entry.getValue());
        }

        log.info("Email message actually was not sent due to enabled developer profile");
    }

}
