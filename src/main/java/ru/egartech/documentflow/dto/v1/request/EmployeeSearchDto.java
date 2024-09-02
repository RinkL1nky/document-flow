package ru.egartech.documentflow.dto.v1.request;

import lombok.Data;

import java.beans.ConstructorProperties;

@Data
public class EmployeeSearchDto {

    @ConstructorProperties({"username", "first_name", "last_name", "email"})
    public EmployeeSearchDto(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    private final String username;

    private final String firstName;

    private final String lastName;

    private final String email;

}
