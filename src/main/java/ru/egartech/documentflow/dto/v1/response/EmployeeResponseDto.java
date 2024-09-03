package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EmployeeResponseDto {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private List<String> authorities;

}
