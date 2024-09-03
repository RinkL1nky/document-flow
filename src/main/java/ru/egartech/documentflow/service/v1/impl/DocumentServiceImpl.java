package ru.egartech.documentflow.service.v1.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.entity.Document;
import ru.egartech.documentflow.entity.FileMetadata;
import ru.egartech.documentflow.exception.NotFoundException;
import ru.egartech.documentflow.exception.auth.ForbiddenException;
import ru.egartech.documentflow.repository.DocumentRepository;
import ru.egartech.documentflow.repository.DocumentTypeRepository;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.search.GenericSpecificationBuilder;
import ru.egartech.documentflow.service.v1.DocumentService;
import ru.egartech.documentflow.service.v1.SimpleStorageService;
import ru.egartech.documentflow.service.v1.mapper.DocumentMapper;
import ru.egartech.documentflow.util.AuthenticationFacade;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentMapper documentMapper;
    private final SimpleStorageService simpleStorageService;
    private final TransactionTemplate transactionTemplate;
    private final AuthenticationFacade authenticationFacade;

    @Transactional(readOnly = true)
    @Override
    public DocumentResponseDto getDocument(Long documentId) {
        Document document = documentRepository.findWithSignaturesById(documentId)
                .orElseThrow(() -> new NotFoundException("documentId"));
        return documentMapper.convertToDto(document);
    }

    @Transactional(readOnly = true)
    @Override
    public PageWrapper<DocumentResponseDto> getDocumentPage(DocumentSearchDto searchDto, Pageable pageable) {
        Specification<Document> specification = new GenericSpecificationBuilder<Document>()
                .withContains("name", searchDto.getName())
                .withContains("comment", searchDto.getComment())
                .withEquals("type.id", searchDto.getTypeId())
                .withEquals("issuer.id", searchDto.getIssuerId())
                .withLessThanOrEquals("createdAt", searchDto.getBeforeCreatedAt())
                .withGreaterThanOrEquals("createdAt", searchDto.getAfterCreatedAt())
                .build();
        Page<Document> documentPage = documentRepository.findAll(specification, pageable);

        return PageWrapper.<DocumentResponseDto>builder()
                .totalPages(documentPage.getTotalPages())
                .totalItems(documentPage.getTotalElements())
                .pageNumber(documentPage.getNumber())
                .itemCount(documentPage.getSize())
                .items(documentMapper.convertToDto(documentPage.getContent()))
                .build();
    }

    @Override
    public DocumentResponseDto createDocument(DocumentPostRequestDto documentRequestDto) {
        FileMetadata newFileMeta = simpleStorageService.applyDraft(documentRequestDto.getFileId());

        try {
            Document committedDocument = transactionTemplate.execute(result -> {
                simpleStorageService.setFileExpired(documentRequestDto.getFileId());

                Document document = documentMapper.convertToEntity(documentRequestDto);
                document.setType(documentTypeRepository.getReferenceById(documentRequestDto.getTypeId()));
                document.setFile(newFileMeta);
                return documentRepository.save(document);
            });
            return documentMapper.convertToDto(committedDocument);

        } catch (Exception transactionException) { // rollback external service changes
            simpleStorageService.setFileExpired(newFileMeta.getId()); // let the scheduler clean up
            throw transactionException;
        }
    }

    @Transactional
    @Override
    public void updateDocument(Long documentId, DocumentPutRequestDto documentRequestDto) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("documentId"));

        if(!authenticationFacade.isCurrentEmployee(document.getIssuer())) {
            throw new ForbiddenException();
        }

        documentMapper.updateEntity(documentRequestDto, document);
        document.setType(documentTypeRepository.getReferenceById(documentRequestDto.getTypeId()));
    }

    @Override
    public void updateDocumentFile(Long documentId, Long fileId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("documentId"));

        simpleStorageService.moveFile(fileId, document.getFile().getId());
        simpleStorageService.setFileExpired(fileId);
    }

    @Transactional
    @Override
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if(document == null) {
            return;
        }

        if(!authenticationFacade.isCurrentEmployee(document.getIssuer())) {
            throw new ForbiddenException();
        }

        documentRepository.deleteById(documentId);
        documentRepository.expireTreeFiles(documentId);
    }

}
