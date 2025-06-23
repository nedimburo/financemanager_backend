package org.finance.financemanager.files.mappers;

import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.files.payloads.FileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "fileId"),
            @Mapping(source = "created", target = "createdDate")
    })
    FileResponseDto toDto(FileEntity file);

    List<FileResponseDto> toDtoList(List<FileEntity> files);
}
