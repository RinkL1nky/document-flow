package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.entity.Task;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Поиск задачи по её предку (в выполняемой транзакции он должен быть единственным)
     * и документу, с которыми он связан. Указанный документ передаётся для однозначного
     * поиска задачи. Задача-корень не имеет предка (он равен null), таким образом,
     * у каждой корневой задачи, предок равен null, но документ различен.
     * @param parentId ID задачи-предка, по которому необходимо найти задачу-потомка
     * @param documentId ID документа
     * @return найденная задача
     */
    @Query("SELECT t FROM Task t " +
            "WHERE t.parent.id = :parentId " +
            "OR (t.parent.id IS NULL AND :parentId IS NULL AND t.document.id = :documentId)") // null != null
    Optional<Task> findChildByParentIdAndDocumentId(Long parentId, Long documentId);

    /**
     * Переместить задачу-потомка к другой задаче-предку. Указанный документ передаётся для однозначного
     * обновления задачи. Задача-корень не имеет предка (он равен null), таким образом,
     * у каждой корневой задачи предок равен null, но документ различен.
     * @param documentId ID документа
     * @param parentId ID задачи-предка, по которому необходимо найти задачу-потомка
     * @param newParentId ID новой задачи-предка, на которую необходимо переопределить задачу-потомка
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.parent.id = :newParentId " +
            "WHERE t.document.id = :documentId " +
            "AND ((t.parent.id IS NULL AND :parentId IS NULL) OR t.parent.id = :parentId)") // null != null
    void rebaseTask(Long documentId, Long parentId, Long newParentId);

    /**
     * Обновить статус указанной задачи.
     * @param taskId ID задачи
     * @param newStatus новый статус
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.id = :taskId")
    void updateTaskStatus(Long taskId, Task.Status newStatus);

    /**
     * Обновить статус задачи по её предку.
     * @param parentId ID задачи-предка, по которому необходимо найти задачу-потомка
     * @param newStatus новый статус для задачи-потомка
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.parent.id = :parentId")
    void updateChildTaskStatus(Long parentId, Task.Status newStatus);

    /**
     * Вычислить количество невыполненных заданий для дерева документов.
     * Задания для корневого документа считаться не будут.
     * @param rootDocumentId корневой документ, с которого начать рекурсивный обход всех
     *                       веток дерева
     * @return число невыполненных заданий для дерева
     */
    @Query(value =
    """
    WITH RECURSIVE document_tree AS (
        SELECT d.* FROM document d
        WHERE d.parent_id = :rootDocumentId
        UNION ALL
        SELECT d.* FROM document d
        JOIN document_tree ON d.parent_id = document_tree.id
    )
    SELECT COUNT(*) FROM task t
    JOIN document_tree n ON t.document_id = n.id
    WHERE t.status != 'COMPLETED'
    """, nativeQuery = true)
    Long countNotCompletedSubsidiaryTasks(Long rootDocumentId);

    /**
     * Активировать цепочку задач для указанного документа. Если в цепочке нет
     * активного задания, станет активным следующее задание после последнего выполненного.
     * Если уже существует активное задание, никаких изменений совершено не будет.
     * Если нет ни одного выполненного задания, станет активным первое в цепочке задание.
     * @param documentId ID документа, цепочку заданий для которого необходимо активировать
     */
    @Transactional
    @Modifying
    @Query("UPDATE Task child SET child.status = 'IN_PROGRESS' " +
            "WHERE child.id IN (" +
            "SELECT child.id FROM Task child JOIN Task parent " +
            "ON child.parent.id = parent.id " +
            "AND parent.status = 'COMPLETED' " +
            "AND child.status IN ('WAITING', 'REJECTED') " +
            "WHERE child.document.id = :documentId" +
            ")" +
            "OR child.parent.id IS NULL AND child.status IN ('WAITING', 'REJECTED')")
    void activateDocumentTaskChain(Long documentId);

}
