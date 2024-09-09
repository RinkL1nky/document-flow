package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.entity.Document;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    /**
     * Поиск одним запросом документа и загрузка всех подписей к нему.
     * @param documentId ID документа
     * @return найденный документ
     */
    @EntityGraph(attributePaths = {"signatures"})
    Optional<Document> findWithSignaturesById(Long documentId);

    /**
     * Поиск одним запросом документа и загрузка мета-данных его файла.
     * @param documentId ID документа
     * @return найденный документ
     */
    @EntityGraph(attributePaths = {"file"})
    Optional<Document> findWithFileById(Long documentId);

    /**
     * Получение ID всех файлов, связанных с деревом документов: по одному файлу на каждый файл.
     * @param rootDocumentId корневой документ, с которого начать рекурсивный обход всех
     *                       веток дерева
     * @return список идентификаторов файлов
     */
    @Query(value =
    """
    WITH RECURSIVE document_tree AS (
        SELECT d.id, d.file_id FROM document d
        WHERE d.id = :rootDocumentId
        UNION ALL
        SELECT d.id, d.file_id FROM document d
        JOIN document_tree ON d.parent_id = document_tree.id
    )
    SELECT f.id FROM file_metadata f JOIN document_tree d ON f.id = d.file_id
    """, nativeQuery = true)
    List<Long> getTreeFileIds(Long rootDocumentId);

    /**
     * Окончить срок действия всех файлов, связанных с деревом документов.
     * @param rootDocumentId корневой документ, с которого начать рекурсивный обход всех
     *                       веток дерева
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value =
    """
    WITH RECURSIVE document_tree AS (
        SELECT d.id, d.file_id FROM document d
        WHERE d.id = :rootDocumentId
        UNION ALL
        SELECT d.id, d.file_id FROM document d
        JOIN document_tree ON d.parent_id = document_tree.id
    )
    UPDATE file_metadata f SET created_at = NOW() WHERE f.id IN (SELECT node.file_id FROM document_tree node)
    """, nativeQuery = true)
    void expireTreeFiles(Long rootDocumentId);

}
