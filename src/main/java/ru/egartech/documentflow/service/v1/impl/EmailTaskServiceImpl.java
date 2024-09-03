package ru.egartech.documentflow.service.v1.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.dto.v1.request.EmailTaskRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmailTaskResponseDto;
import ru.egartech.documentflow.entity.EmailTask;
import ru.egartech.documentflow.entity.Task;
import ru.egartech.documentflow.exception.auth.ForbiddenException;
import ru.egartech.documentflow.service.emailclient.EmailClient;
import ru.egartech.documentflow.exception.email.EmailSendingException;
import ru.egartech.documentflow.exception.email.MessageTemplateNotFoundException;
import ru.egartech.documentflow.service.emailtemplateprocessor.MessageTemplateProcessor;
import ru.egartech.documentflow.exception.ApplicationException;
import ru.egartech.documentflow.exception.NotFoundException;
import ru.egartech.documentflow.repository.EmailTaskRepository;
import ru.egartech.documentflow.repository.TaskRepository;
import ru.egartech.documentflow.service.v1.EmailTaskService;
import ru.egartech.documentflow.service.v1.TaskService;
import ru.egartech.documentflow.service.v1.mapper.EmailTaskMapper;
import ru.egartech.documentflow.util.AuthenticationFacade;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmailTaskServiceImpl implements EmailTaskService {
    private final EmailTaskRepository emailTaskRepository;
    private final TaskRepository taskRepository;
    private final EmailTaskMapper emailTaskMapper;
    private final TaskService taskService;
    private final EmailClient emailClient;
    private final AuthenticationFacade authenticationFacade;
    private final Map<String, MessageTemplateProcessor> messageTemplateProcessorMap;

    @Transactional(readOnly = true)
    @Override
    public EmailTaskResponseDto getTaskDetails(Long taskId) {
        EmailTask emailTask = emailTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        return emailTaskMapper.convertToDto(emailTask);
    }

    @Transactional
    @Override
    public EmailTaskResponseDto createTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));

        if(!authenticationFacade.isCurrentEmployee(task.getIssuer())) {
            throw new ForbiddenException();
        }
        if(!getSupportedMessageTemplates().contains(detailsRequestDto.getTemplateName())) {
            throw new MessageTemplateNotFoundException();
        }

        EmailTask emailTask = emailTaskMapper.convertToEntity(detailsRequestDto);
        emailTask.setTask(task);
        emailTaskRepository.save(emailTask);

        return emailTaskMapper.convertToDto(emailTask);
    }

    @Transactional
    @Override
    public void updateTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto) {
        EmailTask emailTask = emailTaskRepository.findWithRootById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));

        if(!authenticationFacade.isCurrentEmployee(emailTask.getTask().getIssuer())) {
            throw new ForbiddenException();
        }
        if(!getSupportedMessageTemplates().contains(detailsRequestDto.getTemplateName())) {
            throw new MessageTemplateNotFoundException();
        }

        emailTaskMapper.updateEntity(detailsRequestDto, emailTask);
    }

    @Override
    public void deleteTaskDetails(Long taskId) {
        emailTaskRepository.deleteById(taskId);
    }

    @Override
    public void sendEmail(Long taskId) {
        EmailTask emailTask = emailTaskRepository.findWithRootById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        taskService.validateTaskCompleting(emailTask.getTask(), Task.Type.CONFIRM_EMAIL_SENDING);

        MessageTemplateProcessor messageTemplateProcessor =
                messageTemplateProcessorMap.get(emailTask.getTemplateName() + "TemplateProcessor");
        if(messageTemplateProcessor == null) {
            throw new ApplicationException();
        }

        try {
            emailClient.sendMessage(messageTemplateProcessor.process(emailTask));
        } catch (MessagingException exception) {
            throw new EmailSendingException(exception);
        }

        taskService.makeTaskCompleted(taskId);
    }

    public List<String> getSupportedMessageTemplates() {
        return messageTemplateProcessorMap.keySet()
                .stream()
                .map(string -> string.replace("TemplateProcessor", ""))
                .toList();
    }

}
