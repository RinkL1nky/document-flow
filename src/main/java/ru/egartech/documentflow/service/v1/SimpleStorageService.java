package ru.egartech.documentflow.service.v1;

import ru.egartech.documentflow.dto.v1.request.FileDraftRequest;
import ru.egartech.documentflow.dto.v1.request.FileMetadataRequestDto;
import ru.egartech.documentflow.dto.v1.response.FileDownloadResponse;
import ru.egartech.documentflow.dto.v1.response.FileUploadResponse;
import ru.egartech.documentflow.entity.FileMetadata;

import java.io.InputStream;
import java.time.LocalDateTime;

public interface SimpleStorageService {

    /**
     * Получить URL для скачивания указанного файла.
     * @param fileId ID файла
     * @return DTO для скачивания файла
     * @throws ru.egartech.documentflow.exception.NotFoundException если файл не найден
     * @throws ru.egartech.documentflow.exception.file.FileIsExpiredException если срок действия файла истёк
     * @throws ru.egartech.documentflow.exception.file.FileStorageException если сформировать URL не удалось
     */
    FileDownloadResponse getDownloadUrl(Long fileId);

    /**
     * Сформировать данные для загрузки файла на сервер согласно найденным мета-данным.
     * @param fileId ID файла
     * @return DTO с данными для загрузки
     * @throws ru.egartech.documentflow.exception.NotFoundException если файл не найден
     * @throws ru.egartech.documentflow.exception.file.FileIsExpiredException если срок действия файла истёк
     * @throws ru.egartech.documentflow.exception.file.FileStorageException если сформировать данные
     * для загрузки не удалось
     * @see SimpleStorageService#getUploadFormData(FileMetadata)
     */
    FileUploadResponse getUploadFormData(Long fileId);

    /**
     * Сформировать данные для загрузки файла на сервер согласно мета-данным.
     * @param file мета-данные файла
     * @return DTO с данными для загрузки
     * @throws ru.egartech.documentflow.exception.file.FileIsExpiredException если срок действия файла истёк
     * @throws ru.egartech.documentflow.exception.file.FileStorageException если сформировать данные
     * для загрузки не удалось
     */
    FileUploadResponse getUploadFormData(FileMetadata file);

    /**
     * Сформировать данные для загрузки временного файла (черновика) на сервер.
     * @param draftRequestDto мета-данные загружаемого файла
     * @return DTO с данными для загрузки
     */
    FileUploadResponse createDraft(FileDraftRequest draftRequestDto);

    /**
     * Сохранить новый файл, имеющий бессрочный срок действия, используя содержание временного файла (черновика).
     * @param draftFileId ID файла-черновика
     * @return мета-данные созданного файла
     * @throws ru.egartech.documentflow.exception.NotFoundException если черновик не найден
     * @throws ru.egartech.documentflow.exception.file.FileIsExpiredException если срок действия черновика истёк
     * @see SimpleStorageService#moveFile(FileMetadata, FileMetadata) 
     */
    FileMetadata applyDraft(Long draftFileId);

    /**
     * Переместить содержимое одного файла в другую локацию согласно мета-данным.
     * @param sourceFileId ID исходного файла
     * @param destinationFileId ID целевого файла
     * @throws ru.egartech.documentflow.exception.NotFoundException если мета-данные
     * исходного файла или целевого файла не были найдены
     * @see SimpleStorageService#moveFile(FileMetadata, FileMetadata)
     */
    void moveFile(Long sourceFileId, Long destinationFileId);

    /**
     * Скопировать содержимое одного файла в другую локацию согласно мета-данным.
     * @param sourceFile мета-данные исходного файла
     * @param destinationFile мета-данные целевого файла
     * @throws ru.egartech.documentflow.exception.file.FileIsExpiredException если срок действия
     * исходного файла или целевого файла истёк
     * @throws ru.egartech.documentflow.exception.file.FileStorageException если скопировать файл
     * не удалось
     * @see SimpleStorageService#moveFile(FileMetadata, FileMetadata)
     */
    void moveFile(FileMetadata sourceFile, FileMetadata destinationFile);

    /**
     * Обновить мета-данные для указанного файла. Изменённые мета-данные отразятся
     * на формировании URL для скачивания или загрузки на сервер.
     * @param fileId ID файла
     * @param fileMetadataRequestDto DTO для изменения мета-данных
     * @throws ru.egartech.documentflow.exception.NotFoundException если файл не найден
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является владельцем файла
     */
    void updateFileMetadata(Long fileId, FileMetadataRequestDto fileMetadataRequestDto);

    /**
     * Удалить данные о файле в базе данных и на внешнем сервисе.
     * @param fileId ID файла
     * @see SimpleStorageService#deleteFile(FileMetadata)
     */
    void deleteFile(Long fileId);

    /**
     * Удалить данные о файле в базе данных и на внешнем сервисе.
     * @param file мета-данные файла
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не является владельцем файла
     * @throws ru.egartech.documentflow.exception.file.FileStorageException если удалить файл
     * на стороннем сервисе не удалось
     */
    void deleteFile(FileMetadata file);

    /**
     * Получить байтовый поток для указанного файла напрямую со стороннего сервиса.
     * По завершении чтения, поток должен быть закрыт.
     * @param file мета-данные файла
     * @return поток для чтения
     */
    InputStream getFileStream(FileMetadata file);

    /**
     * Обновить срок действия указанного файла. По истечении срока действия файла,
     * планировщик удалит данные файла из базы данных, а также сам файл на внешнем сервисе.
     * @param fileId ID файла
     * @param newExpiration временная точка для истечения срока действия файла.
     *                      Если указан null, срок действия файла становится бессрочными.
     */
    void setFileExpiration(Long fileId, LocalDateTime newExpiration);

    /**
     * Окончить срок действия указанного файла. По истечении срока действия файла,
     * планировщик удалит данные файла из базы данных, а также сам файл на внешнем сервисе.
     * @param fileId ID файла
     * @see SimpleStorageService#setFileExpiration(Long, LocalDateTime)
     */
    void setFileExpired(Long fileId);

}
