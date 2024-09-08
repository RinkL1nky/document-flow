package ru.egartech.documentflow.service.v1.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentTypeResponseDto;
import ru.egartech.documentflow.entity.DocumentType;
import ru.egartech.documentflow.exception.NotFoundException;
import ru.egartech.documentflow.repository.DocumentTypeRepository;
import ru.egartech.documentflow.responsewrapper.PageWrapper;
import ru.egartech.documentflow.search.GenericSpecificationBuilder;
import ru.egartech.documentflow.service.v1.DocumentTypeService;
import ru.egartech.documentflow.service.v1.mapper.DocumentTypeMapper;

@RequiredArgsConstructor
@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;

    @Cacheable("documentTypeResponse")
    @Transactional(readOnly = true)
    @Override
    public DocumentTypeResponseDto getDocumentType(Long documentTypeId) {
        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new NotFoundException("documentTypeId"));
        return documentTypeMapper.convertToDto(documentType);
    }

    @Transactional(readOnly = true)
    @Override
    public PageWrapper<DocumentTypeResponseDto> getDocumentTypePage(DocumentTypeSearchDto searchDto, Pageable pageable) {
        Specification<DocumentType> specification = new GenericSpecificationBuilder<DocumentType>()
                .withContains("name", searchDto.getName())
                .build();
        Page<DocumentType> documentTypePage = documentTypeRepository.findAll(specification, pageable);

        return PageWrapper.<DocumentTypeResponseDto>builder()
                .totalPages(documentTypePage.getTotalPages())
                .totalItems(documentTypePage.getTotalElements())
                .pageNumber(documentTypePage.getNumber())
                .itemCount(documentTypePage.getSize())
                .items(documentTypeMapper.convertToDto(documentTypePage.getContent()))
                .build();
    }

    @Override
    public DocumentTypeResponseDto createDocumentType(DocumentTypeRequestDto requestDto) {
        DocumentType documentType = documentTypeRepository.save(documentTypeMapper.convertToEntity(requestDto));
        return documentTypeMapper.convertToDto(documentType);
    }

    @CacheEvict(value = "documentTypeResponse", key = "#documentTypeId")
    @Transactional
    @Override
    public void updateDocumentType(Long documentTypeId, DocumentTypeRequestDto requestDto) {
        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new NotFoundException("documentTypeId"));
        documentTypeMapper.updateEntity(requestDto, documentType);
    }

    @CacheEvict("documentTypeResponse")
    @Transactional
    @Override
    public void deleteDocumentType(Long documentTypeId) {
        documentTypeRepository.deleteById(documentTypeId);
    }
}
