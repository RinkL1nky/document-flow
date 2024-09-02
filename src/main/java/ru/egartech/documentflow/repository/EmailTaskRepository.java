package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.egartech.documentflow.entity.EmailTask;

import java.util.Optional;

@Repository
public interface EmailTaskRepository extends JpaRepository<EmailTask, Long> {

    @EntityGraph(attributePaths = {"task"})
    Optional<EmailTask> findWithRootById(Long taskId);

}
