package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.GrantedAuthority;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmployeeResponseDto;
import ru.egartech.documentflow.entity.Employee;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee convertToEntity(EmployeeRequestDto requestDto);

    EmployeeResponseDto convertToDto(Employee employee);

    List<EmployeeResponseDto> convertToDto(List<Employee> employeeList);

    void updateEntity(EmployeeRequestDto requestDto, @MappingTarget Employee employee);

    default String convertAuthority(GrantedAuthority authority) {
        return authority.getAuthority();
    }

}
