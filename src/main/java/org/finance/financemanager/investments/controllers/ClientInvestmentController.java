package org.finance.financemanager.investments.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.investments.payloads.InvestmentDetailsResponseDto;
import org.finance.financemanager.investments.payloads.InvestmentRequestDto;
import org.finance.financemanager.investments.payloads.InvestmentResponseDto;
import org.finance.financemanager.investments.services.InvestmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("client/investments")
@Tags(value = {@Tag(name = "Client | Investments"), @Tag(name = OPERATION_ID_NAME + "ClientInvestments")})
public class ClientInvestmentController {

    private final InvestmentService service;

    @GetMapping("/")
    public Page<InvestmentResponseDto> getUsersInvestments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getUsersInvestments(pageable);
    }

    @GetMapping("/search")
    public Page<InvestmentResponseDto> searchUsersInvestments(
            @RequestParam String investmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.searchUsersInvestments(investmentName, pageable);
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponseDto> getInvestmentById(@PathVariable String investmentId) {
        return service.getInvestmentById(investmentId);
    }

    @PostMapping("/create")
    public ResponseEntity<InvestmentResponseDto> createInvestment(@RequestBody InvestmentRequestDto investmentRequest) {
        return service.createInvestment(investmentRequest);
    }

    @PatchMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponseDto> updateInvestment(@PathVariable String investmentId , @RequestBody InvestmentRequestDto investmentRequest) {
        return service.updateInvestment(investmentId, investmentRequest);
    }

    @DeleteMapping("/{investmentId}")
    public ResponseEntity<DeleteResponseDto> deleteInvestment(@PathVariable String investmentId) {
        return service.deleteInvestment(investmentId);
    }

    @GetMapping("/details")
    public ResponseEntity<InvestmentDetailsResponseDto> getInvestmentDetails() {
        return service.getInvestmentDetails();
    }
}
