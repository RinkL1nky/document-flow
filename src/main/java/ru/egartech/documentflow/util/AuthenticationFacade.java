package ru.egartech.documentflow.util;

import ru.egartech.documentflow.entity.Employee;

import java.util.Optional;

public interface AuthenticationFacade {

    Optional<Employee> findCurrentEmployee();

    Employee getCurrentEmployee();

    boolean isCurrentEmployee(Employee employee);

}
