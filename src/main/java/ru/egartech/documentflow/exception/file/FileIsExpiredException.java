package ru.egartech.documentflow.exception.file;

import org.springframework.http.HttpStatus;

public class FileIsExpiredException extends FileStorageException {

    public FileIsExpiredException() {
        super(HttpStatus.CONFLICT, "FILE_IS_EXPIRED", "File was expired", null);
    }

}
