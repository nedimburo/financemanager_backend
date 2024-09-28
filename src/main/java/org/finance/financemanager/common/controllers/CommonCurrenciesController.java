package org.finance.financemanager.common.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.CurrencyRateDto;
import org.finance.financemanager.common.services.FxRatesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("common/currencies")
@Tags(value = {@Tag(name = "Common | Currencies"), @Tag(name = OPERATION_ID_NAME + "CommonCurrencies")})
public class CommonCurrenciesController {

    private final FxRatesService service;

    @GetMapping("/")
    public List<String> getCurrencies() {
        return service.getCurrencies();
    }

    @GetMapping("/exchange-rates")
    public List<CurrencyRateDto> getExchangeRates() {
        return service.getExchangeRates();
    }
}
