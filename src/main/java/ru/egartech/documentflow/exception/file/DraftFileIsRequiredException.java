package ru.egartech.documentflow.exception.file;

import org.springframework.http.HttpStatus;

public class DraftFileIsRequiredException extends FileStorageException {

    public DraftFileIsRequiredException() {
        super(HttpStatus.CONFLICT, "DRAFT_FILE_IS_REQUIRED", "Provided file is not draft", null);
    }

}
