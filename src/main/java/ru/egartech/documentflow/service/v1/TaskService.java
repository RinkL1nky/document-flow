package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskSearchDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.entity.Task;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;

import java.util.List;

public interface TaskService {

    /**
     * Получить задачу по её идентификатору.
     * @param taskId ID задачи
     * @return найденная задача
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     */
    TaskResponseDto getTask(Long taskId);

    /**
     * Получить все задачи согласно выбранным фильтрам и пагинации.
     * @param searchDto DTO для фильтров
     * @param pageable DTO для параметров пагинации
     * @return результаты поиска (список задач)
     */
    PageWrapper<TaskResponseDto> getTaskPage(TaskSearchDto searchDto, Pageable pageable);

    /**
     * Добавить новую задачу в базу данных. При существовании цепочки задач
     * для указанного документа, задача должна быть встроена в существующую
     * последовательность задач и получить задачу-предка и задачу-потомка.
     * @param taskRequestDto DTO для создания задачи
     * @return созданная задача в базе данных с её ID
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь не имеет право
     * редактировать цепочку задач для документа
     * @throws ru.egartech.documentflow.exception.task.TaskChainIntegrityViolation если добавляемая задача
     * нарушает текущую последовательность задач для документа
     */
    TaskResponseDto createTask(TaskPostRequestDto taskRequestDto);

    /**
     * Создать новую цепочку задач и встроить её в существующую (при её наличии). Каждая задача
     * в созданной цепочке становится предком для задачи, идущей следующей.
     * @param documentId ID документа для задач
     * @param rootParentId ID задачи, которая станет предком для первой задачи в созданной цепочке
     * @param chainTaskDtoList Список DTO для задач цепочки
     * @return созданные задачи
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь не имеет право
     * редактировать цепочку задач для документа
     * @throws ru.egartech.documentflow.exception.task.TaskChainIntegrityViolation если добавляемые задачи
     * нарушают текущую последовательность задач для документа
     */
    List<TaskResponseDto> createTaskChain(Long documentId, Long rootParentId,
                                          List<TaskChainItemRequestDto> chainTaskDtoList);

    /**
     * Обновить данные задачи. Позволяет переместить задачу на другую позицию в цепочке задач
     * для документа, но не позволяет переместить задачу в цепочку задач другого документа.
     * @param taskId ID задачи
     * @param taskRequestDto DTO для изменения задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь не имеет право
     * редактировать задачу для документа
     * @throws ru.egartech.documentflow.exception.task.WrongTaskStatusException если статус задачи
     * не предполагает её редактирование
     */
    void updateTask(Long taskId, TaskPutRequestDto taskRequestDto);

    /**
     * Удалить задачу.
     * @param taskId ID задачи
     * @see TaskService#deleteTask(Task)
     */
    void deleteTask(Long taskId);

    /**
     * Удалить задачу.
     * @param task удаляемая задача
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь не имеет право
     * удалять задачу для документа
     */
    void deleteTask(Task task);

    /**
     * Отклонить выполнение задачи. Задача станет доступна к редактированию сотруднику, выпустившему её.
     * @param taskId ID задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если задача не обращена
     * к данному пользователю
     * @throws ru.egartech.documentflow.exception.task.WrongTaskStatusException если статус задачи
     * не предполагает отказ от выполнения
     */
    void rejectTask(Long taskId);

    /**
     * Запустить выполнение цепочки задач для документа. Если в цепочке нет активного задания,
     * станет активным следующее задание после последнего выполненного. Если нет ни одного выполненного задания,
     * станет активным первое в цепочке задание. Если уже существует активное задание,
     * никаких изменений совершено не будет. Таким образом, операция является идемпотентной.
     * @param documentId ID задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь не имеет право
     * запустить выполнение задач для данного документа
     */
    void activateDocumentTaskChain(Long documentId);

    /**
     * Добавить к документу подпись и выполнить соответствующую задачу.
     * @param taskId ID задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @see TaskService#validateTaskCompleting(Task, Task.Type)
     * @see TaskService#makeTaskCompleted(Long) 
     */
    void signDocument(Long taskId);

    /**
     * Редактировать файл документа (применить файл-черновик с изменениями) и выполнить соответствующую задачу.
     * @param taskId ID задачи
     * @param fileId ID файла-черновика
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @see TaskService#validateTaskCompleting(Task, Task.Type)
     * @see DocumentService#updateDocumentFile(Long, Long)
     */
    void editDocumentFile(Long taskId, Long fileId);

    /**
     * Прикрепить выпущенный дочерний документ к документу в задаче и, соответственно, её выполнить.
     * @param taskId ID задачи
     * @param childDocumentId ID дочернего документа
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @see TaskService#validateTaskCompleting(Task, Task.Type)
     * найти задачу или дочерний документ
     */
    void linkChildDocument(Long taskId, Long childDocumentId);

    /**
     * Подтвердить готовность выпущенных дочерних документов и выполнить соответствующую задачу.
     * @param taskId ID задачи
     * @throws ru.egartech.documentflow.exception.NotFoundException если задача не найдена
     * @throws ru.egartech.documentflow.exception.task.SubsidiaryTasksNotCompletedException если для дочерних
     * @see TaskService#validateTaskCompleting(Task, Task.Type)
     * документов существуют невыполненные задачи.
     */
    void confirmSubsidiaryTasksCompletion(Long taskId);

    /**
     * Пометить задачу как выполненную и сделать активной следующую задачу.
     * @param taskId ID задачи
     */
    void makeTaskCompleted(Long taskId);

    /**
     * Осуществить проверку готовности задачи для выполнения.
     * @param task выполняемая задача
     * @param taskType тип задачи (действие для выполнения задачи)
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если задача не обращена
     * к данному пользователю
     * @throws ru.egartech.documentflow.exception.task.WrongTaskTypeException если задача не предполагает
     * выполнение согласно данному действию.
     */
    void validateTaskCompleting(Task task, Task.Type taskType);

}
