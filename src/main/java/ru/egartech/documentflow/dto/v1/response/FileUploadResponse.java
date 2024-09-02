package ru.egartech.documentflow.dto.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class FileUploadResponse {

    private Long id;

    private String uploadUrl;

    private Map<String, String> formData;

}
