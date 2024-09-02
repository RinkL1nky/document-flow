package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.dto.v1.request.EmployeeSearchDto;
import ru.egartech.documentflow.dto.v1.response.EmployeeResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;

import java.util.List;

public interface EmployeeService extends UserDetailsService {

    EmployeeResponseDto getEmployee(Long id);

    PageWrapper<EmployeeResponseDto> getEmployeePage(EmployeeSearchDto searchDto, Pageable pageable);

    EmployeeResponseDto createEmployee(EmployeeRequestDto registrationForm);

    void updateEmployee(Long employeeId, EmployeeRequestDto employeeRequestDto);

    void deleteEmployee(Long id);

}
