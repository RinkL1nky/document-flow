package ru.egartech.documentflow.dto.v1.request;

import lombok.Data;

import java.beans.ConstructorProperties;

@Data
public class DocumentTypeSearchDto {

    @ConstructorProperties({"name"})
    public DocumentTypeSearchDto(String name) {
        this.name = name;
    }

    private final String name;

}
