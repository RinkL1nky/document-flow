package ru.egartech.documentflow.controller.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentTypeResponseDto;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;
import ru.egartech.documentflow.service.v1.DocumentTypeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/document-types")
public class DocumentTypeController {
    private final DocumentTypeService documentTypeService;

    @GetMapping("/document-type/{documentTypeId}")
    public DocumentTypeResponseDto getDocumentType(@PathVariable @Positive Short documentTypeId) {
        return documentTypeService.getDocumentType(Long.valueOf(documentTypeId));
    }

    @GetMapping
    public PageWrapper<DocumentTypeResponseDto> getDocumentTypePage(DocumentTypeSearchDto searchDto, Pageable pageable) {
        return documentTypeService.getDocumentTypePage(searchDto, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/document-type")
    public DocumentTypeResponseDto createDocumentType(
            @RequestBody @Valid DocumentTypeRequestDto documentTypeRequestDto) {
        return documentTypeService.createDocumentType(documentTypeRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/document-type/{documentTypeId}")
    public void updateDocumentType(@PathVariable @Positive Short documentTypeId,
                                   @RequestBody @Valid DocumentTypeRequestDto documentTypeRequestDto) {
        documentTypeService.updateDocumentType(Long.valueOf(documentTypeId), documentTypeRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/document-type/{documentTypeId}")
    public void deleteDocumentType(@PathVariable @Positive Short documentTypeId) {
        documentTypeService.deleteDocumentType(Long.valueOf(documentTypeId));
    }

}
