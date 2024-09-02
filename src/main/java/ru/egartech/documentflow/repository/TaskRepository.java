package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.egartech.documentflow.entity.Task;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t FROM Task t " +
            "WHERE t.parent.id = :parentId " +
            "OR (t.parent.id IS NULL AND :parentId IS NULL AND t.document.id = :documentId)") // null != null
    Optional<Task> findChildByParentIdAndDocumentId(Long parentId, Long documentId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.parent.id = :newParentId " +
            "WHERE t.document.id = :documentId " +
            "AND ((t.parent.id IS NULL AND :parentId IS NULL) OR t.parent.id = :parentId)") // null != null
    void rebaseTask(Long documentId, Long parentId, Long newParentId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.id = :taskId")
    void updateTaskStatus(Long taskId, Task.Status newStatus);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.parent.id = :parentId")
    void updateChildTaskStatus(Long parentId, Task.Status newStatus);

    @Query(value =
    """
    WITH RECURSIVE document_tree AS (
        SELECT d.* FROM document d
        WHERE d.id = :rootDocumentId
        UNION ALL
        SELECT d.* FROM document d
        JOIN document_tree ON d.parent_id = document_tree.id
    )
    SELECT COUNT(*) FROM task t
    JOIN document_tree n ON t.document_id = n.id
    WHERE t.status != 'COMPLETED'
    """, nativeQuery = true)
    Long countNotCompletedSubsidiaryTasks(Long rootDocumentId);

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
