package ru.egartech.documentflow.service.emailclient;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.Map;

@Builder
@Data
public class EmailMessage {

    private final String recipient;

    private final String subject;

    private final String body;

    private final boolean html;

    private final Map<String, ? extends Resource> attachments;

}
