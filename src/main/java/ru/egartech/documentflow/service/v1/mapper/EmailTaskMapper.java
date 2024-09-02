package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.egartech.documentflow.dto.v1.request.EmailTaskRequestDto;
import ru.egartech.documentflow.dto.v1.response.EmailTaskResponseDto;
import ru.egartech.documentflow.entity.EmailTask;

@Mapper(componentModel = "spring")
public interface EmailTaskMapper {

    EmailTask convertToEntity(EmailTaskRequestDto requestDto);

    @Mapping(source = "task.id", target = "taskId")
    EmailTaskResponseDto convertToDto(EmailTask emailTask);

    void updateEntity(EmailTaskRequestDto requestDto, @MappingTarget EmailTask emailTask);

}
