package org.finance.financemanager.currency_rates.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.services.FxRatesService;
import org.finance.financemanager.currency_rates.entities.CurrencyRateEntity;
import org.finance.financemanager.currency_rates.mappers.CurrencyRateMapper;
import org.finance.financemanager.currency_rates.payloads.CurrencyRateDto;
import org.finance.financemanager.currency_rates.repositories.CurrencyRateRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private final CurrencyRateRepository repository;
    private final FxRatesService fxRatesService;
    private final CacheManager cacheManager;
    private final CurrencyRateMapper currencyRateMapper;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void scheduledUpdateExchangeRates() {
        updateExchangeRates();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationStartup() {
        updateExchangeRates();
    }


    @Transactional
    public void updateExchangeRates() {
        int attempts = 0;
        List<CurrencyRateDto> currencyRates = null;

        while (attempts < 3) {
            currencyRates = fxRatesService.getExchangeRates();
            if (currencyRates != null && !currencyRates.isEmpty()) {
                break;
            }
            attempts++;
            log.warn("Failed to fetch exchange rates, attempt {}", attempts);
        }

        if (currencyRates != null && !currencyRates.isEmpty()) {
            List<CurrencyRateEntity> existingRates = repository.findAll();
            Map<String, CurrencyRateEntity> existingMap = existingRates.stream()
                    .collect(Collectors.toMap(CurrencyRateEntity::getCurrency, Function.identity()));

            Set<String> incomingCurrencies = currencyRates.stream()
                    .map(CurrencyRateDto::getCurrency)
                    .collect(Collectors.toSet());

            for (CurrencyRateDto dto : currencyRates) {
                CurrencyRateEntity existing = existingMap.get(dto.getCurrency());
                if (existing != null) {
                    existing.setRate(dto.getRate());
                    existing.setUpdated(LocalDateTime.now());
                    repository.save(existing);
                } else {
                    CurrencyRateEntity newRate = new CurrencyRateEntity();
                    newRate.setCurrency(dto.getCurrency());
                    newRate.setRate(dto.getRate());
                    newRate.setCreated(LocalDateTime.now());
                    newRate.setUpdated(LocalDateTime.now());
                    repository.save(newRate);
                }
            }

            for (CurrencyRateEntity entity : existingRates) {
                if (!incomingCurrencies.contains(entity.getCurrency())) {
                    repository.delete(entity);
                }
            }

            log.info("Exchange rates successfully updated.");

            Objects.requireNonNull(cacheManager.getCache("currencies")).clear();
        } else {
            log.warn("Failed to fetch exchange rates after 3 attempts. Keeping existing data.");
        }
    }

    @Cacheable(value = "currencies", key = "'allCurrencies'")
    @Transactional
    public List<CurrencyRateDto> getCurrencyRates() {
        List<CurrencyRateEntity> currencies = repository.findAll();
        return currencies.stream()
                .map(currencyRateMapper::toDto)
                .collect(Collectors.toList());
    }
}
