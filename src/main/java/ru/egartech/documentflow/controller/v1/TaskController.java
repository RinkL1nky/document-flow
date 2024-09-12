package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskSearchDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;
import ru.egartech.documentflow.service.v1.TaskService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/task/{taskId}")
    public TaskResponseDto getTask(@PathVariable @Positive Long taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping
    public PageWrapper<TaskResponseDto> getTaskPage(TaskSearchDto searchDto, Pageable pageable) {
        return taskService.getTaskPage(searchDto, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/task")
    public TaskResponseDto createTask(@RequestBody @Valid TaskPostRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<TaskResponseDto> createTaskChain(
            @RequestParam("document_id") @NotNull @Positive Long documentId,
            @RequestParam("root_parent_id") @Positive Long rootParentId,
            @RequestBody @NotEmpty List<@Valid TaskChainItemRequestDto> chainTaskDtoList) {
        return taskService.createTaskChain(documentId, rootParentId, chainTaskDtoList);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/task/{taskId}")
    public void updateTask(@PathVariable @Positive Long taskId,
                           @RequestBody @Valid TaskPutRequestDto taskRequestDto) {
        taskService.updateTask(taskId, taskRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/task/{taskId}")
    public void deleteTask(@PathVariable @Positive Long taskId) {
        taskService.deleteTask(taskId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/task/{taskId}/reject")
    public void rejectTask(@PathVariable @Positive Long taskId) {
        taskService.rejectTask(taskId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/activate-chain")
    public void activateTaskChain(@RequestParam("document_id") @Positive Long documentId) {
        taskService.activateDocumentTaskChain(documentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/task/{taskId}/complete/sign-document")
    public void signDocument(@PathVariable @Positive Long taskId) {
        taskService.signDocument(taskId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/task/{taskId}/complete/edit-document")
    public void editDocument(@PathVariable @Positive Long taskId,
                             @RequestParam("file_id") @Positive Long fileId) {
        taskService.editDocumentFile(taskId, fileId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/task/{taskId}/complete/link-document")
    public void linkDocument(@PathVariable @Positive Long taskId,
                             @RequestParam("child_document_id") @Positive Long childDocumentId) {
        taskService.linkChildDocument(taskId, childDocumentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/task/{taskId}/complete/confirm-subsidiary-tasks-completion")
    public void confirmSubsidiaryTasksCompletion(@PathVariable @Positive Long taskId) {
        taskService.confirmSubsidiaryTasksCompletion(taskId);
    }

}
