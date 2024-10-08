package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;
import ru.egartech.documentflow.service.v1.DocumentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("/document/{id}")
    public DocumentResponseDto getDocument(@PathVariable @Positive Long id)  {
        return documentService.getDocument(id);
    }

    @GetMapping
    public PageWrapper<DocumentResponseDto> getDocumentPage(DocumentSearchDto searchDto, Pageable pageable) {
        return documentService.getDocumentPage(searchDto, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/document")
    public DocumentResponseDto createDocument(@RequestBody @Valid DocumentPostRequestDto documentInfo) {
        return documentService.createDocument(documentInfo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/document/{id}")
    public void update(@PathVariable @Positive Long id,
                       @RequestBody @Valid DocumentPutRequestDto documentInfo) {
        documentService.updateDocument(id, documentInfo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/document/{id}")
    public void delete(@PathVariable @Positive Long id) {
        documentService.deleteDocument(id);
    }

}
