package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.service.v1.EmployeeService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/register")
public class AuthController {
    private final EmployeeService employeeService;

    @GetMapping
    public String register() {
        return "registration";
    }

    @PostMapping
    public String register(@Valid EmployeeRequestDto registrationForm) {
        employeeService.createEmployee(registrationForm);
        return "redirect:/login";
    }

}
