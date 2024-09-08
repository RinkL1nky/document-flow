package ru.egartech.documentflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.entity.Employee;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    /**
     * Поиск сущности сотрудника и загрузка его ролей.
     * @param employeeId ID сотрудника
     * @return найденный сотрудник
     */
    @EntityGraph(attributePaths = {"authorities"})
    Optional<Employee> findWithAuthoritiesById(Long employeeId);

    /**
     * Поиск сущности сотрудника и загрузка его ролей.
     * @param username имя пользователя у сотрудника
     * @return найденный сотрудник
     */
    @EntityGraph(attributePaths = {"authorities"})
    Optional<Employee> findByUsername(String username);

}
