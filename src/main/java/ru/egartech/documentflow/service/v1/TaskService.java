package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskSearchDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.entity.Task;
import ru.egartech.documentflow.responsewrapper.PageWrapper;

import java.util.List;

public interface TaskService {

    TaskResponseDto getTask(Long taskId);

    PageWrapper<TaskResponseDto> getTaskPage(TaskSearchDto searchDto, Pageable pageable);

    TaskResponseDto createTask(TaskPostRequestDto taskRequestDto);

    List<TaskResponseDto> createTaskChain(Long documentId, Long rootParentId, List<TaskChainItemRequestDto> chainTaskDtoList);

    void updateTask(Long taskId, TaskPutRequestDto taskRequestDto);

    void deleteTask(Long taskId);

    void rejectTask(Long taskId);

    void activateDocumentTaskChain(Long documentId);

    void signDocument(Long taskId);

    void editDocumentFile(Long taskId, Long fileId);

    void linkChildDocument(Long taskId, Long childDocumentId);

    void confirmSubsidiaryTasksCompletion(Long taskId);

    void makeTaskCompleted(Long taskId);

    void validateTaskCompleting(Task task, Task.Type taskType);

}
