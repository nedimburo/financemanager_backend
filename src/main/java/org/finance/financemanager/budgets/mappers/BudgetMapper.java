package org.finance.financemanager.budgets.mappers;

import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.payloads.BudgetSpecificResponseDto;
import org.finance.financemanager.files.mappers.FileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FileMapper.class)
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "budgetId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    BudgetResponseDto toDto(BudgetEntity budget);

    @Mappings({
            @Mapping(source = "id", target = "budgetId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate"),
            @Mapping(source = "updated", target = "updatedDate"),
            @Mapping(source = "files", target = "files")
    })
    BudgetSpecificResponseDto toSpecificDto(BudgetEntity budget);

    BudgetEntity toEntity(BudgetRequestDto request);
}
