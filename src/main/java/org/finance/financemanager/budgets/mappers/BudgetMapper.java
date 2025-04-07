package org.finance.financemanager.budgets.mappers;

import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "budgetId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    BudgetResponseDto toDto(BudgetEntity budget);

    BudgetEntity toEntity(BudgetRequestDto request);
}
