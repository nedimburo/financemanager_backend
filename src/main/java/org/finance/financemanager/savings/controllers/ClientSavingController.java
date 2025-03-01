package org.finance.financemanager.savings.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.savings.payloads.SavingAmountResponseDto;
import org.finance.financemanager.savings.payloads.SavingDetailsResponseDto;
import org.finance.financemanager.savings.payloads.SavingRequestDto;
import org.finance.financemanager.savings.payloads.SavingResponseDto;
import org.finance.financemanager.savings.services.SavingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public Page<SavingResponseDto> getUsersSavings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getUsersSavings(pageable);
    }

    @GetMapping("/search")
    public Page<SavingResponseDto> searchUsersSavings(
            @RequestParam String goalName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.searchUsersSavings(goalName, pageable);
    }

    @GetMapping("/{savingId}")
    public ResponseEntity<SavingResponseDto> getSavingById(@PathVariable String savingId) {
        return service.getSavingById(savingId);
    }

    @PostMapping("/create")
    public ResponseEntity<SavingResponseDto> createSaving(@RequestBody SavingRequestDto savingRequest) {
        return service.createSaving(savingRequest);
    }

    @PatchMapping("/{savingId}")
    public ResponseEntity<SavingResponseDto> updateSaving(@PathVariable String savingId , @RequestBody SavingRequestDto savingRequest) {
        return service.updateSaving(savingId, savingRequest);
    }

    @DeleteMapping("/{savingId}")
    public ResponseEntity<SuccessResponseDto> deleteSaving(@PathVariable String savingId) {
        return service.deleteSaving(savingId);
    }

    @GetMapping("/details")
    public ResponseEntity<SavingDetailsResponseDto> getSavingDetails() {
        return service.getSavingDetails();
    }

    @PatchMapping("/edit-saved-amount/{savingId}")
    public ResponseEntity<SavingAmountResponseDto> editSavedAmount(
            @PathVariable String savingId,
            @RequestParam BigDecimal savedAmount) {
        return service.editSavedAmount(savingId, savedAmount);
    }
}
