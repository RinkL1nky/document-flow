package ru.egartech.documentflow.service.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskSearchDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.entity.Document;
import ru.egartech.documentflow.entity.DocumentSignature;
import ru.egartech.documentflow.entity.Task;
import ru.egartech.documentflow.exception.*;
import ru.egartech.documentflow.exception.auth.ForbiddenException;
import ru.egartech.documentflow.exception.task.SubsidiaryTasksNotCompletedException;
import ru.egartech.documentflow.exception.task.TaskChainIntegrityViolation;
import ru.egartech.documentflow.exception.task.WrongTaskStatusException;
import ru.egartech.documentflow.exception.task.WrongTaskTypeException;
import ru.egartech.documentflow.repository.DocumentRepository;
import ru.egartech.documentflow.repository.EmployeeRepository;
import ru.egartech.documentflow.repository.TaskRepository;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.search.GenericSpecificationBuilder;
import ru.egartech.documentflow.util.AuthenticationFacade;
import ru.egartech.documentflow.service.v1.mapper.TaskMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentService documentService;
    private final TransactionTemplate transactionTemplate;
    private final AuthenticationFacade authenticationFacade;

    @Transactional(readOnly = true)
    @Override
    public TaskResponseDto getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));

        return taskMapper.convertToDto(task);
    }

    @Transactional(readOnly = true)
    @Override
    public PageWrapper<TaskResponseDto> getTaskPage(TaskSearchDto searchDto, Pageable pageable) {
        Specification<Task> specification = new GenericSpecificationBuilder<Task>()
                .withContains("name", searchDto.getName())
                .withContains("comment", searchDto.getComment())
                .withEquals("document.id", searchDto.getDocumentId())
                .withGreaterThanOrEquals("deadline", searchDto.getFromDeadline())
                .withLessThanOrEquals("deadline", searchDto.getToDeadline())
                .withEquals("appointee.id", searchDto.getAppointeeId())
                .withEquals("type", searchDto.getType())
                .withEquals("status", searchDto.getStatus())
                .withEquals("issuer.id", searchDto.getIssuerId())
                .withGreaterThanOrEquals("createdAt", searchDto.getFromCreatedAt())
                .withLessThanOrEquals("createdAt", searchDto.getToCreatedAt())
                .build();
        Page<Task> taskPage = taskRepository.findAll(specification, pageable);

        return PageWrapper.<TaskResponseDto>builder()
                .totalPages(taskPage.getTotalPages())
                .totalItems(taskPage.getTotalElements())
                .pageNumber(taskPage.getNumber())
                .itemCount(taskPage.getSize())
                .items(taskMapper.convertToDto(taskPage.getContent()))
                .build();
    }

    @Transactional
    @Override
    public TaskResponseDto createTask(TaskPostRequestDto taskRequestDto) {
        Optional<Task> rootChildTaskWrapper = taskRepository
                .findChildByParentIdAndDocumentId(taskRequestDto.getParentId(), taskRequestDto.getDocumentId());

        rootChildTaskWrapper.ifPresent(oldRootChildTask -> {
            if(!authenticationFacade.isCurrentEmployee(oldRootChildTask.getIssuer())) { // only issuer of document can issue tasks for it
                throw new ForbiddenException();
            }
            if(!oldRootChildTask.getDocument().getId().equals(taskRequestDto.getDocumentId())) { // the whole task chain must be addressed to one document
                throw new TaskChainIntegrityViolation();
            }
            if(!oldRootChildTask.getStatus().equals(Task.Status.WAITING)) { // new task must be built into uncompleted task pair
                throw new TaskChainIntegrityViolation();
            }
        });

        Task task = taskMapper.convertToEntity(taskRequestDto);
        task.setDocument(documentRepository.getReferenceById(taskRequestDto.getDocumentId()));
        task.setAppointee(employeeRepository.getReferenceById(taskRequestDto.getAppointeeId()));
        task.setStatus(Task.Status.WAITING);
        if(taskRequestDto.getParentId() != null) {
            task.setParent(taskRepository.getReferenceById(taskRequestDto.getParentId()));
        }
        Task savedTask = taskRepository.saveAndFlush(task);

        // associate child task with the new task
        rootChildTaskWrapper.ifPresent(oldRootChildTask -> oldRootChildTask.setParent(savedTask));

        return taskMapper.convertToDto(task);
    }

    @Transactional
    @Override
    public List<TaskResponseDto> createTaskChain(Long documentId, Long rootParentId,
                                                 List<TaskChainItemRequestDto> taskRequestDtoList) {
        Optional<Task> childTaskWrapper = taskRepository
                .findChildByParentIdAndDocumentId(rootParentId, documentId);

        childTaskWrapper.ifPresent(oldRootChildTask -> {
            if(!authenticationFacade.isCurrentEmployee(oldRootChildTask.getIssuer())) { // only issuer of document can issue tasks for it
                throw new ForbiddenException();
            }
            if(!oldRootChildTask.getDocument().getId().equals(documentId)) { // the whole task chain must be addressed to one document
                throw new TaskChainIntegrityViolation();
            }
            if(!oldRootChildTask.getStatus().equals(Task.Status.WAITING)) { // new task chain must be built into uncompleted task pair
                throw new TaskChainIntegrityViolation();
            }
        });

        List<Task> newTasks = taskMapper.convertToEntity(taskRequestDtoList);
        Task firstTask = newTasks.getFirst();
        if(rootParentId != null) { // first task of the chain can be associated with specified parent task
            firstTask.setParent(taskRepository.getReferenceById(rootParentId));
        }
        for(int i = 0; i < newTasks.size(); i++) {
            Task task = newTasks.get(i);
            TaskChainItemRequestDto taskDto = taskRequestDtoList.get(i);
            task.setDocument(documentRepository.getReferenceById(documentId));
            task.setAppointee(employeeRepository.getReferenceById(taskDto.getAppointeeId()));
            task.setStatus(Task.Status.WAITING);
            if(i > 0) { // each task except the first one will be child of previous one
                task.setParent(newTasks.get(i - 1));
            }
        }
        final List<Task> savedTaskChain = taskRepository.saveAllAndFlush(newTasks);

        childTaskWrapper.ifPresent(oldRootChildTask -> oldRootChildTask
                .setParent(taskRepository.getReferenceById(savedTaskChain.getLast().getId()))
        ); // associate child task with the end of the added chain

        return taskMapper.convertToDto(newTasks);
    }

    @Transactional
    @Override
    public void updateTask(Long taskId, TaskPutRequestDto taskRequestDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));

        if(!authenticationFacade.isCurrentEmployee(task.getIssuer())) {
            throw new ForbiddenException();
        }
        if(!List.of(Task.Status.WAITING, Task.Status.REJECTED).contains(task.getStatus())) {
            throw new WrongTaskStatusException();
        }

        taskMapper.updateEntity(taskRequestDto, task);
        task.setAppointee(employeeRepository.getReferenceById(taskRequestDto.getAppointeeId()));

        Long oldParentTaskId = task.getParent() != null ? task.getParent().getId() : null;
        if(Objects.equals(oldParentTaskId, taskRequestDto.getParentId())) {
            return;
        }

        // rebase old child task to old parent task (upper one) and therefore close the gap due to moved task
        // (adjective "old" refers to old position of updated task)
        taskRepository.rebaseTask(task.getDocument().getId(), task.getId(), oldParentTaskId);

        taskRepository.findChildByParentIdAndDocumentId(taskRequestDto.getParentId(), task.getDocument().getId())
                .ifPresent(oldChildTask -> oldChildTask.setParent(task)); // associate new child task with moved task

        if(taskRequestDto.getParentId() != null) {
            task.setParent(taskRepository.getReferenceById(taskRequestDto.getParentId()));
        } else {
            task.setParent(null);
        }
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId) {
        Task deletedTask = taskRepository.findById(taskId).orElse(null);
        if(deletedTask == null) {
            return;
        }

        if(!authenticationFacade.isCurrentEmployee(deletedTask.getIssuer())) {
            throw new ForbiddenException();
        }
        if(!List.of(Task.Status.WAITING, Task.Status.REJECTED).contains(deletedTask.getStatus())) {
            throw new WrongTaskStatusException();
        }

        if(deletedTask.getParent() != null) {
            taskRepository.rebaseTask(
                    deletedTask.getDocument().getId(),
                    deletedTask.getId(),
                    deletedTask.getParent().getId()
            );
        }
        taskRepository.deleteById(taskId);
    }

    @Transactional
    @Override
    public void rejectTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));

        if(!authenticationFacade.isCurrentEmployee(task.getAppointee())) {
            throw new ForbiddenException();
        }
        if(!task.getStatus().equals(Task.Status.IN_PROGRESS)) {
            throw new WrongTaskStatusException();
        }

        task.setStatus(Task.Status.REJECTED);
    }

    @Transactional
    @Override
    public void activateDocumentTaskChain(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("documentId"));
        if(!authenticationFacade.isCurrentEmployee(document.getIssuer())) {
            throw new ForbiddenException();
        }
        taskRepository.activateDocumentTaskChain(documentId);
    }

    @Transactional
    @Override
    public void signDocument(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        validateTaskCompleting(task, Task.Type.SIGN);
        task.getDocument().getSignatures().add(new DocumentSignature(task.getAppointee()));

        transactionTemplate.executeWithoutResult(result -> makeTaskCompleted(taskId));
    }

    @Override
    public void editDocumentFile(Long taskId, Long fileId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        validateTaskCompleting(task, Task.Type.EDIT);
        documentService.updateDocumentFile(task.getDocument().getFile().getId(), fileId);

        transactionTemplate.executeWithoutResult(result -> makeTaskCompleted(taskId));
    }

    @Transactional
    @Override
    public void linkChildDocument(Long taskId, Long childDocumentId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        validateTaskCompleting(task, Task.Type.ISSUE_NEW_DOCUMENT);
        Document childDocument = documentRepository.findById(childDocumentId)
                .orElseThrow(() -> new NotFoundException("childDocumentId"));
        childDocument.setParent(task.getDocument());

        transactionTemplate.executeWithoutResult(result -> makeTaskCompleted(taskId));
    }

    @Transactional
    @Override
    public void confirmSubsidiaryTasksCompletion(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("taskId"));
        validateTaskCompleting(task, Task.Type.CONFIRM_SUBSIDIARY_TASKS_COMPLETION);
        if(taskRepository.countNotCompletedSubsidiaryTasks(task.getDocument().getId()) > 0) {
            throw new SubsidiaryTasksNotCompletedException();
        }

        transactionTemplate.executeWithoutResult(result -> makeTaskCompleted(taskId));
    }

    @Transactional
    public void makeTaskCompleted(Long taskId) {
        taskRepository.updateTaskStatus(taskId, Task.Status.COMPLETED);
        taskRepository.updateChildTaskStatus(taskId, Task.Status.IN_PROGRESS);
    }

    public void validateTaskCompleting(Task task, Task.Type type) {
        if(!authenticationFacade.isCurrentEmployee(task.getAppointee())) {
            throw new ForbiddenException();
        }
        if(!task.getType().equals(type)) {
            throw new WrongTaskTypeException();
        }
        if(!task.getStatus().equals(Task.Status.IN_PROGRESS)) {
            throw new WrongTaskStatusException();
        }
    }

}
