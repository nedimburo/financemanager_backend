package org.finance.financemanager.currency_rates.mappers;

import org.finance.financemanager.currency_rates.entities.CurrencyRateEntity;
import org.finance.financemanager.currency_rates.payloads.CurrencyRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CurrencyRateMapper {
    CurrencyRateMapper INSTANCE = Mappers.getMapper(CurrencyRateMapper.class);

    CurrencyRateDto toDto(CurrencyRateEntity currencyRateEntity);
}
