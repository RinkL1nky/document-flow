package ru.egartech.documentflow.service.v1;

import ru.egartech.documentflow.dto.v1.request.EmailTaskRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmailTaskResponseDto;
import ru.egartech.documentflow.entity.EmailTask;
import ru.egartech.documentflow.entity.Task;

import java.util.List;

public interface EmailTaskService {

    /**
     * Получить детали отправки электронной почты для соответствующей задачи.
     * @param taskId ID задачи
     * @return DTO для деталей задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если детали задачи не найдены
     */
    EmailTaskResponseDto getTaskDetails(Long taskId);

    /**
     * Добавить детали отправки электронной почты для соответствующей задачи в базу данных.
     * @param detailsRequestDto DTO для деталей задачи
     * @return созданные детали для указанной задачи
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является создателем задачи
     * @throws ru.egartech.documentflow.exception.email.MessageTemplateNotFoundException если шаблон
     * для письма не найден
     */
    EmailTaskResponseDto createTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto);

    /**
     * Обновить детали отправки электронной почты для соответствующей задачи.
     * @param taskId ID задачи
     * @param detailsRequestDto DTO для изменения деталей
     * @throws ru.egartech.documentflow.exception.NotFoundException если детали задачи не найдены
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является создателем задачи
     * @throws ru.egartech.documentflow.exception.email.MessageTemplateNotFoundException если шаблон
     * для письма не найден
     */
    void updateTaskDetails(Long taskId, EmailTaskRequestDto detailsRequestDto);

    /**
     * Удалить детали отправки электронной почты для соответствующей задачи.
     * @param taskId ID задачи
     */
    void deleteTaskDetails(Long taskId);

    /**
     * Отправка письма по электронной почте согласно указанной задаче. В случае успеха,
     * задача получит статус Выполненной.
     * @param taskId ID задачи, которая имеет детали для отправки письма
     * @throws ru.egartech.documentflow.exception.NotFoundException если детали
     * к указанной задаче не были найдены
     * @throws ru.egartech.documentflow.exception.email.EmailSendingException если не удалось отправить письмо
     * @throws ru.egartech.documentflow.exception.email.MessageTemplateProcessorNotFound если шаблонизатор письма
     * для соответствующего шаблона не был найден
     * @see ru.egartech.documentflow.service.emailtemplateprocessor.MessageTemplateProcessor#process(EmailTask)
     * @see TaskService#validateTaskCompleting(Task, Task.Type)
     * @see TaskService#makeTaskCompleted(Long) 
     */
    void sendEmail(Long taskId);

    /**
     * Получить доступные шаблоны для подготовки отправки письма.
     * @return список шаблонов
     */
    List<String> getSupportedMessageTemplates();

}
