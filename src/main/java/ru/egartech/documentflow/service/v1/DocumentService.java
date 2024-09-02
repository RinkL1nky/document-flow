package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;

public interface DocumentService {

    DocumentResponseDto getDocument(Long documentId);

    PageWrapper<DocumentResponseDto> getDocumentPage(DocumentSearchDto searchDto, Pageable pageable);

    DocumentResponseDto createDocument(DocumentPostRequestDto requestDto);

    void updateDocument(Long documentId, DocumentPutRequestDto requestDto);

    void updateDocumentFile(Long documentId, Long fileId);

    void deleteDocument(Long documentId);

}
