package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskSearchDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.responsewrapper.WrappedResponse;
import ru.egartech.documentflow.service.v1.TaskService;

import java.util.List;

@WrappedResponse
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

    @PostMapping("/task")
    public TaskResponseDto createTask(@RequestBody @Valid TaskPostRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @PostMapping
    public List<TaskResponseDto> createTaskChain(
            @RequestParam("document_id") @NotNull @Positive Long documentId,
            @RequestParam("root_parent_id") @Positive Long rootParentId,
            @RequestBody @NotEmpty List<@Valid TaskChainItemRequestDto> chainTaskDtoList) {
        return taskService.createTaskChain(documentId, rootParentId, chainTaskDtoList);
    }

    @PutMapping("/task/{taskId}")
    public void updateTask(@PathVariable @Positive Long taskId,
                           @RequestBody @Valid TaskPutRequestDto taskRequestDto) {
        taskService.updateTask(taskId, taskRequestDto);
    }

    @DeleteMapping("/task/{taskId}")
    public void deleteTask(@PathVariable @Positive Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PostMapping("/task/{taskId}/reject")
    public void rejectTask(@PathVariable @Positive Long taskId) {
        taskService.rejectTask(taskId);
    }

    @PostMapping("/activate-chain")
    public void activateTaskChain(@RequestParam("document_id") @Positive Long documentId) {
        taskService.activateDocumentTaskChain(documentId);
    }

    @PostMapping("/task/{taskId}/complete/sign-document")
    public void signDocument(@PathVariable @Positive Long taskId) {
        taskService.signDocument(taskId);
    }

    @PostMapping("/task/{taskId}/complete/edit-document")
    public void editDocument(@PathVariable @Positive Long taskId,
                             @RequestParam("file_id") @Positive Long fileId) {
        taskService.editDocumentFile(taskId, fileId);
    }

    @PostMapping("/task/{taskId}/complete/link-document")
    public void linkDocument(@PathVariable @Positive Long taskId,
                             @RequestParam("child_document_id") @Positive Long childDocumentId) {
        taskService.linkChildDocument(taskId, childDocumentId);
    }

    @PostMapping("/task/{taskId}/complete/confirm-subsidiary-tasks-completion")
    public void confirmSubsidiaryTasksCompletion(@PathVariable @Positive Long taskId) {
        taskService.confirmSubsidiaryTasksCompletion(taskId);
    }

}
