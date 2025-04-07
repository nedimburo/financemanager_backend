package org.finance.financemanager.investments.mappers;

import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.payloads.InvestmentRequestDto;
import org.finance.financemanager.investments.payloads.InvestmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {
    InvestmentMapper INSTANCE = Mappers.getMapper(InvestmentMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "investmentId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    InvestmentResponseDto toDto(InvestmentEntity investment);

    InvestmentEntity toEntity(InvestmentRequestDto request);
}
