package org.finance.financemanager.savings.mappers;

import org.finance.financemanager.files.mappers.FileMapper;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.payloads.SavingRequestDto;
import org.finance.financemanager.savings.payloads.SavingResponseDto;
import org.finance.financemanager.savings.payloads.SavingSpecificResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FileMapper.class)
public interface SavingMapper {
    SavingMapper INSTANCE = Mappers.getMapper(SavingMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "savingId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    SavingResponseDto toDto(SavingEntity saving);

    @Mappings({
            @Mapping(source = "id", target = "savingId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate"),
            @Mapping(source = "updated", target = "updatedDate"),
            @Mapping(source = "files", target = "files")
    })
    SavingSpecificResponseDto toSpecificDto(SavingEntity saving);

    SavingEntity toEntity(SavingRequestDto request);
}
