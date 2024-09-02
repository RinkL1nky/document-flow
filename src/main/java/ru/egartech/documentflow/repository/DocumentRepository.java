package ru.egartech.documentflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.egartech.documentflow.entity.Document;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @EntityGraph(attributePaths = {"signatures"})
    @Override
    Page<Document> findAll(Specification<Document> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"signatures"})
    Optional<Document> findWithSignaturesById(Long documentId);

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
