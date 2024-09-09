package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentTypeSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentTypeResponseDto;
import ru.egartech.documentflow.responsewrapper.PageWrapper;

public interface DocumentTypeService {

    /**
     * Получить тип документа по его идентификатору.
     * @param documentTypeId ID типа документа
     * @return DTO для типа документа
     * @throws ru.egartech.documentflow.exception.NotFoundException если тип документа не найден
     */
    DocumentTypeResponseDto getDocumentType(Long documentTypeId);

    /**
     * Найти все типы документов согласно выбранным фильтрам и пагинации.
     * @param searchDto DTO для фильтров
     * @param pageable DTO для параметров пагинации
     * @return результаты поиска (список типов документов)
     */
    PageWrapper<DocumentTypeResponseDto> getDocumentTypePage(DocumentTypeSearchDto searchDto, Pageable pageable);

    /**
     * Добавить тип документа в базу данных.
     * @param requestDto DTO для создания типа документа
     * @return созданный тип документа в базе данных с его ID
     */
    DocumentTypeResponseDto createDocumentType(DocumentTypeRequestDto requestDto);

    /**
     * Обновить данные типа документа.
     * @param documentTypeId ID типа документа
     * @param requestDto DTO для изменения типа документа
     * @throws ru.egartech.documentflow.exception.NotFoundException если тип документа не найден
     */
    void updateDocumentType(Long documentTypeId, DocumentTypeRequestDto requestDto);

    /**
     * Удалить тип документа.
     * @param documentTypeId ID типа документа
     */
    void deleteDocumentType(Long documentTypeId);

}
