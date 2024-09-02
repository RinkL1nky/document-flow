package ru.egartech.documentflow.service.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.egartech.documentflow.dto.v1.request.TaskChainItemRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPostRequestDto;
import ru.egartech.documentflow.dto.v1.request.TaskPutRequestDto;
import ru.egartech.documentflow.dto.v1.response.TaskResponseDto;
import ru.egartech.documentflow.entity.Task;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task convertToEntity(TaskPostRequestDto requestDto);

    List<Task> convertToEntity(List<TaskChainItemRequestDto> requestList);

    @Mapping(source = "document.id", target = "documentId")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "appointee.id", target = "appointeeId")
    @Mapping(source = "issuer.id", target = "issuerId")
    TaskResponseDto convertToDto(Task task);

    List<TaskResponseDto> convertToDto(List<Task> taskList);

    void updateEntity(TaskPutRequestDto requestDto, @MappingTarget Task task);

}
