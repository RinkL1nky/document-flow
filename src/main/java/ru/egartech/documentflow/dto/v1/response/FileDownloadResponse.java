package ru.egartech.documentflow.dto.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class FileDownloadResponse {

    private final Long id;

    private final String downloadUrl;

}
