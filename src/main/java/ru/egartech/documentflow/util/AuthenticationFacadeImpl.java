package ru.egartech.documentflow.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.egartech.documentflow.entity.Employee;
import ru.egartech.documentflow.exception.auth.AuthenticationCredentialsNotAvailableException;

import java.util.Optional;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Override
    public Optional<Employee> findCurrentEmployee() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(Employee.class::cast);
    }

    @Override
    public Employee getCurrentEmployee() {
        return findCurrentEmployee().orElseThrow(AuthenticationCredentialsNotAvailableException::new);
    }

    @Override
    public boolean isCurrentEmployee(Employee employee) {
        return employee.equals(findCurrentEmployee().orElse(null));
    }

}
