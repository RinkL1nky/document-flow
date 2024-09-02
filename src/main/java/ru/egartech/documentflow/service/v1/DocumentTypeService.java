package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentTypeResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;

import java.util.List;

public interface DocumentTypeService {

    DocumentTypeResponseDto getDocumentType(Long documentTypeId);

    PageWrapper<DocumentTypeResponseDto> getDocumentTypePage(DocumentTypeSearchDto searchDto, Pageable pageable);

    DocumentTypeResponseDto createDocumentType(DocumentTypeRequestDto documentTypeRequestDto);

    void updateDocumentType(Long documentTypeId, DocumentTypeRequestDto documentTypeRequestDto);

    void deleteDocumentType(Long documentTypeId);

}
