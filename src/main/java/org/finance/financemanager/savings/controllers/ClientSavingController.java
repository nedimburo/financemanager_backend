package org.finance.financemanager.savings.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.savings.entities.SavingOrderBy;
import org.finance.financemanager.savings.payloads.SavingAmountResponseDto;
import org.finance.financemanager.savings.payloads.SavingRequestDto;
import org.finance.financemanager.savings.payloads.SavingResponseDto;
import org.finance.financemanager.savings.services.SavingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("client/savings")
@Tags(value = {@Tag(name = "Client | Savings"), @Tag(name = OPERATION_ID_NAME + "ClientSavings")})
public class ClientSavingController {

    private final SavingService service;

    @GetMapping("/")
    public ListResponseDto<SavingResponseDto> getUsersSavings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) SavingOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection
    ){
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsersSavings(pageable, query);
    }

    @GetMapping("/specific")
    public SavingResponseDto getSavingById(@RequestParam String savingId) {
        return service.getSavingById(savingId);
    }

    @PostMapping("/")
    public SavingResponseDto createSaving(@RequestBody SavingRequestDto savingRequest) {
        return service.createSaving(savingRequest);
    }

    @PatchMapping("/")
    public SavingResponseDto updateSaving(@RequestParam String savingId , @RequestBody SavingRequestDto savingRequest) {
        return service.updateSaving(savingId, savingRequest);
    }

    @DeleteMapping("/")
    public SuccessResponseDto deleteSaving(@RequestParam String savingId) {
        return service.deleteSaving(savingId);
    }

    @PatchMapping("/edit-saved-amount/")
    public SavingAmountResponseDto editSavedAmount(
            @RequestParam String savingId,
            @RequestParam BigDecimal savedAmount) {
        return service.editSavedAmount(savingId, savedAmount);
    }
}
