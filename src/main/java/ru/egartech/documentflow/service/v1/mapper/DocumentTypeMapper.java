package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeRequestDto;
import ru.egartech.documentflow.dto.v1.response.DocumentTypeResponseDto;
import ru.egartech.documentflow.entity.DocumentType;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {

    DocumentType convertToEntity(DocumentTypeRequestDto requestDto);

    DocumentTypeResponseDto convertToDto(DocumentType documentType);

    List<DocumentTypeResponseDto> convertToDto(List<DocumentType> documentTypeList);

    void updateEntity(DocumentTypeRequestDto requestDto, @MappingTarget DocumentType documentType);

}
