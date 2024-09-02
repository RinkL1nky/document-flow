package ru.egartech.documentflow.service.v1.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.entity.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentMapperTest {

    private static final String MOCK_STR = "test1234";

    private static final Employee MOCKED_EMPLOYEE = Employee.builder()
            .id(1L)
            .username(MOCK_STR)
            .firstName(MOCK_STR)
            .lastName(MOCK_STR)
            .email(MOCK_STR)
            .password(MOCK_STR)
            .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
            .build();
    private static final DocumentType MOCKED_DOCUMENT_TYPE = DocumentType.builder()
            .id(1L)
            .name(MOCK_STR)
            .build();
    private static final FileMetadata MOCKED_FILE_METADATA = FileMetadata.builder()
            .id(1L)
            .bucket(MOCK_STR)
            .path(MOCK_STR)
            .filename(MOCK_STR)
            .contentType(MOCK_STR)
            .expiresAt(LocalDateTime.now())
            .uploader(MOCKED_EMPLOYEE)
            .createdAt(Instant.now())
            .build();
    private static final Document MOCKED_NO_PARENT_DOCUMENT = Document.builder()
            .id(1L)
            .name(MOCK_STR)
            .comment(MOCK_STR)
            .type(MOCKED_DOCUMENT_TYPE)
            .file(MOCKED_FILE_METADATA)
            .parent(null)
            .signatures(Set.of(new DocumentSignature(MOCKED_EMPLOYEE)))
            .issuer(MOCKED_EMPLOYEE)
            .createdAt(Instant.now())
            .build();

    @Autowired
    private DocumentMapper documentMapper;

    @Test
    void testPostDtoToEntityMapping() {
        DocumentPostRequestDto documentDto = DocumentPostRequestDto.builder()
                .name(MOCK_STR)
                .comment(MOCK_STR)
                .typeId(1L)
                .fileId(1L)
                .build();

        Document document = documentMapper.convertToEntity(documentDto);

        assertNull(documentMapper.convertToEntity(null));
        assertNull(document.getId());
        assertEquals(documentDto.getName(), document.getName());
        assertEquals(documentDto.getComment(), document.getComment());
        assertNull(document.getType());
        assertNull(document.getFile());
    }

    @Test
    void testEntityToDtoMapping() {
        Document document = Document.builder()
                .id(1L)
                .name(MOCK_STR)
                .comment(MOCK_STR)
                .type(MOCKED_DOCUMENT_TYPE)
                .file(MOCKED_FILE_METADATA)
                .parent(MOCKED_NO_PARENT_DOCUMENT)
                .signatures(Set.of(new DocumentSignature(MOCKED_EMPLOYEE)))
                .issuer(MOCKED_EMPLOYEE)
                .createdAt(Instant.now())
                .build();

        DocumentResponseDto documentDto = documentMapper.convertToDto(document);

        assertNull(documentMapper.convertToDto((Document) null));
        assertNull(documentMapper.convertToDto(MOCKED_NO_PARENT_DOCUMENT).getParentId());
        assertEquals(document.getId(), documentDto.getId());
        assertEquals(document.getName(), documentDto.getName());
        assertEquals(document.getComment(), documentDto.getComment());
        assertEquals(MOCKED_DOCUMENT_TYPE.getId(), documentDto.getTypeId());
        assertEquals(MOCKED_NO_PARENT_DOCUMENT.getId(), documentDto.getFileId());
        assertEquals(document.getParent().getId(), documentDto.getParentId());
        assertEquals(document.getSignatures().stream().map(sign -> sign.getSigner().getId()).toList(),
                documentDto.getSignatoryIds());
        assertEquals(document.getIssuer().getId(), documentDto.getIssuerId());
        assertEquals(document.getCreatedAt(), documentDto.getCreatedAt());
    }

    @Test
    void testEntityUpdateMapping() {
        Document oldDocument = Document.builder()
                .id(1L)
                .name(MOCK_STR)
                .comment(MOCK_STR)
                .type(MOCKED_DOCUMENT_TYPE)
                .file(MOCKED_FILE_METADATA)
                .parent(MOCKED_NO_PARENT_DOCUMENT)
                .signatures(Set.of(new DocumentSignature(MOCKED_EMPLOYEE)))
                .issuer(MOCKED_EMPLOYEE)
                .createdAt(Instant.now())
                .build();
        Document document = Document.builder()
                .id(1L)
                .name(MOCK_STR)
                .comment(MOCK_STR)
                .type(MOCKED_DOCUMENT_TYPE)
                .file(MOCKED_FILE_METADATA)
                .parent(MOCKED_NO_PARENT_DOCUMENT)
                .signatures(Set.of(new DocumentSignature(MOCKED_EMPLOYEE)))
                .issuer(MOCKED_EMPLOYEE)
                .createdAt(Instant.now())
                .build();
        DocumentPutRequestDto documentDto = DocumentPutRequestDto.builder()
                .name("changed name")
                .comment("changed comment")
                .typeId(2L)
                .build();

        documentMapper.updateEntity(documentDto, document);

        documentMapper.updateEntity(null, document);

        assertEquals(documentDto.getName(), document.getName());
        assertEquals(documentDto.getComment(), document.getComment());
        assertEquals(oldDocument.getId(), document.getId());
        assertEquals(oldDocument.getType(), document.getType());
        assertEquals(oldDocument.getFile(), document.getFile());
    }

    @Test
    void testEntityListToDtoListMapping() {
        Document document = Document.builder()
                .id(1L)
                .name(MOCK_STR)
                .comment(MOCK_STR)
                .type(MOCKED_DOCUMENT_TYPE)
                .file(MOCKED_FILE_METADATA)
                .parent(MOCKED_NO_PARENT_DOCUMENT)
                .signatures(Set.of(new DocumentSignature(MOCKED_EMPLOYEE)))
                .issuer(MOCKED_EMPLOYEE)
                .createdAt(Instant.now())
                .build();
        List<Document> documentList = Collections.singletonList(document);

        List<DocumentResponseDto> documentDtoList = documentMapper.convertToDto(documentList);

        assertNull(documentMapper.convertToDto((List<Document>) null));
        assertEquals(documentList.size(), documentDtoList.size());

        DocumentResponseDto documentDto = documentDtoList.getFirst();
        assertEquals(document.getId(), documentDto.getId());
        assertEquals(document.getName(), documentDto.getName());
        assertEquals(document.getComment(), documentDto.getComment());
        assertEquals(MOCKED_DOCUMENT_TYPE.getId(), documentDto.getTypeId());
        assertEquals(MOCKED_NO_PARENT_DOCUMENT.getId(), documentDto.getFileId());
        assertEquals(document.getParent().getId(), documentDto.getParentId());
        assertEquals(document.getSignatures().stream().map(sign -> sign.getSigner().getId()).toList(),
                documentDto.getSignatoryIds());
        assertEquals(document.getIssuer().getId(), documentDto.getIssuerId());
        assertEquals(document.getCreatedAt(), documentDto.getCreatedAt());
    }

}
