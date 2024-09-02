package ru.egartech.documentflow.service.v1;

import ru.egartech.documentflow.dto.v1.request.EmailTaskRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmailTaskResponseDto;

import java.util.List;

public interface EmailTaskService {

    EmailTaskResponseDto getTaskDetails(Long taskId);

    EmailTaskResponseDto createTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto);

    void updateTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto);

    void deleteTaskDetails(Long taskId);

    void sendEmail(Long taskId);

    List<String> getSupportedMessageTemplates();

}
