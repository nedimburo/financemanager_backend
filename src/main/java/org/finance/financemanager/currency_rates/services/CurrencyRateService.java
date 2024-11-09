package org.finance.financemanager.currency_rates.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.services.FxRatesService;
import org.finance.financemanager.currency_rates.entities.CurrencyRateEntity;
import org.finance.financemanager.currency_rates.payloads.CurrencyRateDto;
import org.finance.financemanager.currency_rates.repositories.CurrencyRateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private final CurrencyRateRepository repository;
    private final FxRatesService fxRatesService;

    // Turned off when in development
    /*@Scheduled(fixedRate = 172800000)
    @Transactional
    public void updateExchangeRates() {
        List<CurrencyRateDto> currencyRates = fxRatesService.getExchangeRates();

        if (currencyRates != null && !currencyRates.isEmpty()) {
            repository.deleteAll();
            currencyRates.forEach(dto -> {
                CurrencyRateEntity rate = new CurrencyRateEntity();
                rate.setId(UUID.randomUUID().toString());
                rate.setCurrency(dto.getCurrency());
                rate.setRate(dto.getRate());
                rate.setCreated(LocalDateTime.now());
                rate.setUpdated(LocalDateTime.now());
                repository.save(rate);
            });
            System.out.println("Data is valid and saved to database.");
        } else {
            System.out.println("Data is not valid; retaining existing data.");
        }
    }*/

    @Transactional
    public List<CurrencyRateDto> getCurrencyRates() {
        List<CurrencyRateEntity> currencies = repository.findAll();
        return currencies.stream()
                .map(this::formatCurrencyRateResponse)
                .collect(Collectors.toList());
    }

    private CurrencyRateDto formatCurrencyRateResponse(CurrencyRateEntity currencyRateEntity) {
        CurrencyRateDto response = new CurrencyRateDto();
        response.setCurrency(currencyRateEntity.getCurrency());
        response.setRate(currencyRateEntity.getRate());
        return response;
    }
}
