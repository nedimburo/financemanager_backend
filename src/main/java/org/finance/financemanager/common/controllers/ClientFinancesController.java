package org.finance.financemanager.common.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.FinanceInformationResponseDto;
import org.finance.financemanager.common.services.GeneralFinancesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("client/finances")
@Tags(value = {@Tag(name = "Client | Finances"), @Tag(name = OPERATION_ID_NAME + "ClientFinances")})
public class ClientFinancesController {

    private final GeneralFinancesService service;

    @GetMapping("/general-finances")
    public FinanceInformationResponseDto getGeneralFinances() {
        return service.getGeneralFinances();
    }
}
