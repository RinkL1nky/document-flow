package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.entity.Document;
import ru.egartech.documentflow.entity.DocumentSignature;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    Document convertToEntity(DocumentPostRequestDto requestDto);

    @Mapping(source = "type.id", target = "typeId")
    @Mapping(source = "file.id", target = "fileId")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "issuer.id", target = "issuerId")
    @Mapping(source = "signatures", target = "signatoryIds")
    DocumentResponseDto convertToDto(Document document);

    List<DocumentResponseDto> convertToDto(List<Document> documentList);

    void updateEntity(DocumentPutRequestDto requestDto, @MappingTarget Document document);

    List<Long> convertSignatureList(Set<DocumentSignature> signatureList);

    default Long convertSignature(DocumentSignature signature) {
        return signature.getSigner().getId();
    }

}
