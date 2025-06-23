package org.finance.financemanager.investments.mappers;

import org.finance.financemanager.files.mappers.FileMapper;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.payloads.InvestmentRequestDto;
import org.finance.financemanager.investments.payloads.InvestmentResponseDto;
import org.finance.financemanager.investments.payloads.InvestmentSpecificResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FileMapper.class)
public interface InvestmentMapper {
    InvestmentMapper INSTANCE = Mappers.getMapper(InvestmentMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "investmentId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    InvestmentResponseDto toDto(InvestmentEntity investment);

    @Mappings({
            @Mapping(source = "id", target = "investmentId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate"),
            @Mapping(source = "updated", target = "updatedDate"),
            @Mapping(source = "files", target = "files")
    })
    InvestmentSpecificResponseDto toSpecificDto(InvestmentEntity investment);

    InvestmentEntity toEntity(InvestmentRequestDto request);
}
