package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import ru.egartech.documentflow.dto.v1.request.DocumentPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentPutRequestDto;
import ru.egartech.documentflow.dto.v1.request.DocumentSearchDto;
import ru.egartech.documentflow.dto.v1.response.DocumentResponseDto;
import ru.egartech.documentflow.entity.Document;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;

public interface DocumentService {

    /**
     * Получение документа по его идентификатору.
     * @param documentId ID документа
     * @return DTO для документа
     * @throws ru.egartech.documentflow.exception.NotFoundException если документ не найден
     */
    DocumentResponseDto getDocument(Long documentId);

    /**
     * Найти все документы согласно выбранным фильтрам и пагинации.
     * @param searchDto DTO для фильтров
     * @param pageable DTO для параметров пагинации
     * @return результаты поиска (список документов)
     */
    PageWrapper<DocumentResponseDto> getDocumentPage(DocumentSearchDto searchDto, Pageable pageable);

    /**
     * Добавить документ в базу данных.
     * @param requestDto DTO для создания документа
     * @return созданный документ в базе данных с его ID
     * @throws Exception если транзакция для добавления документа в базу данных была прервана
     */
    DocumentResponseDto createDocument(DocumentPostRequestDto requestDto);

    /**
     * Обновить данные документа (за исключением файла).
     * @param documentId ID документа
     * @param requestDto DTO для изменения документа
     * @throws ru.egartech.documentflow.exception.NotFoundException если документ не найден
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является создателем документа
     */
    void updateDocument(Long documentId, DocumentPutRequestDto requestDto);

    /**
     * Обновить содержимое файла у документа.
     * @param documentId ID документа
     * @param draftFileId ID файла-черновика с новым содержимым
     * @see DocumentService#updateDocument(Long, DocumentPutRequestDto)
     */
    void updateDocumentFile(Long documentId, Long draftFileId);

    /**
     * Удалить документ и все его дочерние документы (корень и его дерево).
     * @param documentId ID документа
     * @see DocumentService#deleteDocument(Document) 
     */
    void deleteDocument(Long documentId);

    /**
     * Удалить документ и все его дочерние документы (корень и его дерево).
     * @param document документ
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является создателем документа
     */
    void deleteDocument(Document document);

}
