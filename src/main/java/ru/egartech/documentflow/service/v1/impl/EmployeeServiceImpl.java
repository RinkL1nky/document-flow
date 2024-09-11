package ru.egartech.documentflow.service.v1.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.dto.v1.request.EmployeeSearchDto;
import ru.egartech.documentflow.dto.v1.response.EmployeeResponseDto;
import ru.egartech.documentflow.entity.Employee;
import ru.egartech.documentflow.exception.NotFoundException;
import ru.egartech.documentflow.repository.EmployeeRepository;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.search.GenericSpecificationBuilder;
import ru.egartech.documentflow.service.v1.EmployeeService;
import ru.egartech.documentflow.service.v1.mapper.EmployeeMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public EmployeeResponseDto getEmployee(Long employeeId) {
        Employee employee = employeeRepository.findWithAuthoritiesById(employeeId)
                .orElseThrow(() -> new NotFoundException("employeeId"));

        return employeeMapper.convertToDto(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public PageWrapper<EmployeeResponseDto> getEmployeePage(EmployeeSearchDto searchDto, Pageable pageable) {
        Specification<Employee> specification = new GenericSpecificationBuilder<Employee>()
                .withContains("username", searchDto.getUsername())
                .withContains("firstName", searchDto.getFirstName())
                .withContains("lastName", searchDto.getLastName())
                .withContains("email", searchDto.getEmail())
                .build();
        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);

        return PageWrapper.<EmployeeResponseDto>builder()
                .totalPages(employeePage.getTotalPages())
                .totalItems(employeePage.getTotalElements())
                .pageNumber(employeePage.getNumber())
                .itemCount(employeePage.getSize())
                .items(employeeMapper.convertToDto(employeePage.getContent()))
                .build();
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeMapper.convertToEntity(employeeRequestDto);
        employee.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));
        employee.setEmail(employeeRequestDto.getEmail().toLowerCase());
        employee.getAuthorities().add(new SimpleGrantedAuthority(Employee.Authority.ROLE_USER.name()));

        return employeeMapper.convertToDto(employeeRepository.save(employee));
    }

    @Transactional
    @Override
    public void updateEmployee(Long employeeId, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employeeId"));

        employeeMapper.updateEntity(employeeRequestDto, employee);

        employee.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));
        employee.setEmail(employeeRequestDto.getEmail().toLowerCase());
    }

    @Transactional
    @Override
    public void updateEmployeeAuthorities(Long employeeId, List<String> employeeAuthorities) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employeeId"));

        Set<String> allowedAuthorities = Arrays.stream(Employee.Authority.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        employee.setAuthorities(new HashSet<>());
        employeeAuthorities.stream()
                .filter(allowedAuthorities::contains)
                .forEach(authority -> employee.getAuthorities().add(new SimpleGrantedAuthority(authority)));
    }

    @Transactional
    @Override
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Transactional(readOnly = true)
    @Override
    public Employee loadUserByUsername(String username) throws UsernameNotFoundException {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

}
