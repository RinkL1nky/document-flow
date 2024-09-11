package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.dto.v1.request.EmployeeSearchDto;
import ru.egartech.documentflow.dto.v1.response.EmployeeResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.responsewrapper.WrappedResponse;
import ru.egartech.documentflow.service.v1.EmployeeService;

import java.util.List;

@RequiredArgsConstructor
@WrappedResponse
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/employee/{employeeId}")
    public EmployeeResponseDto getEmployee(@PathVariable @Positive Long employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @GetMapping
    public PageWrapper<EmployeeResponseDto> getEmployeePage(EmployeeSearchDto searchDto, Pageable pageable) {
        return employeeService.getEmployeePage(searchDto, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #employeeId.equals(principal.id)")
    @PutMapping("/employee/{employeeId}")
    public void updateEmployee(@PathVariable @Positive Long employeeId,
                               @RequestBody @Valid EmployeeRequestDto employeeRequestDto) {
        employeeService.updateEmployee(employeeId, employeeRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/employee/{employeeId}/authorities")
    public void updateEmployeeAuthorities(@PathVariable @Positive Long employeeId,
                                          @RequestBody List<String> employeeAuthorities) {
        employeeService.updateEmployeeAuthorities(employeeId, employeeAuthorities);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/employee/{employeeId}")
    public void deleteEmployee(@PathVariable @Positive Long employeeId) {
        employeeService.deleteEmployee(employeeId);
    }

}