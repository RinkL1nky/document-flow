package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.EmailTaskRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmailTaskResponseDto;
import ru.egartech.documentflow.responsewrapper.WrappedResponse;
import ru.egartech.documentflow.service.v1.EmailTaskService;

import java.util.List;

@WrappedResponse
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-tasks")
@RestController
public class EmailTaskController {
    private final EmailTaskService emailTaskService;

    @GetMapping("/email-task/{taskId}")
    public EmailTaskResponseDto getTaskDetails(@PathVariable @Positive Long taskId) {
        return emailTaskService.getTaskDetails(taskId);
    }

    @PostMapping("/email-task/{taskId}")
    public EmailTaskResponseDto createTaskDetails(@PathVariable @Positive Long taskId,
                                                  @RequestBody @Valid EmailTaskRequestDto detailsRequestDto) {
        return emailTaskService.createTaskDetails(taskId, detailsRequestDto);
    }

    @PutMapping("/email-task/{taskId}")
    public void updateTaskDetails(@PathVariable @Positive Long taskId,
                                  @RequestBody @Valid EmailTaskRequestDto detailsRequestDto) {
        emailTaskService.updateTaskDetails(taskId, detailsRequestDto);
    }

    @DeleteMapping("/email-task/{taskId}")
    public void deleteTaskDetails(@PathVariable @Positive Long taskId) {
        emailTaskService.deleteTaskDetails(taskId);
    }

    @PostMapping("/email-task/{taskId}/send-email")
    public void sendEmail(@PathVariable @Positive Long taskId) {
        emailTaskService.sendEmail(taskId);
    }

    @GetMapping("/message-templates")
    public List<String> getSupportedMessageTemplates() {
        return emailTaskService.getSupportedMessageTemplates();
    }

}
