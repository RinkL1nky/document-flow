package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import ru.egartech.documentflow.dto.v1.response.StoredFileMetaResponseDto;
import ru.egartech.documentflow.entity.FileMetadata;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoredFileMetaMapper {

    StoredFileMetaResponseDto convertToDto(FileMetadata documentType);

    List<StoredFileMetaResponseDto> convertToDto(List<FileMetadata> documentTypeList);

}
