package ru.egartech.documentflow.service.emailtemplateprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.egartech.documentflow.repository.DocumentRepository;
import ru.egartech.documentflow.repository.FileMetadataRepository;
import ru.egartech.documentflow.service.emailclient.EmailMessage;
import ru.egartech.documentflow.entity.EmailTask;
import ru.egartech.documentflow.entity.FileMetadata;
import ru.egartech.documentflow.exception.file.FileStorageException;
import ru.egartech.documentflow.service.v1.SimpleStorageService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DocumentTreeReportTemplateProcessor implements MessageTemplateProcessor {
    private final SpringTemplateEngine templateEngine;
    private final DocumentRepository documentRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final SimpleStorageService simpleStorageService;

    @Override
    public EmailMessage process(EmailTask emailTask) {
        Map<String, Object> contextVariables = new HashMap<>();

        Context templateContext = new Context();
        templateContext.setVariables(contextVariables);
        String body = templateEngine.process("email/" + emailTask.getTemplateName(), templateContext);

        Map<String, ByteArrayResource> attachments = new HashMap<>();
        List<FileMetadata> fileMetadataList = fileMetadataRepository.findAllById(
                documentRepository.getTreeFileIds(emailTask.getTask().getDocument().getId())
        );
        for (FileMetadata fileMetadata : fileMetadataList) {
            try (InputStream stream = simpleStorageService.getFileStream(fileMetadata)) {
                attachments.put(fileMetadata.getFilename(), new ByteArrayResource(stream.readAllBytes()));
            } catch (Exception exception) {
                throw new FileStorageException(exception);
            }
        }

        return EmailMessage.builder()
                .recipient(emailTask.getDestinationEmail())
                .subject(emailTask.getSubject())
                .body(body)
                .html(true)
                .attachments(attachments)
                .build();
    }

}
