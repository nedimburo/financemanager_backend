package org.finance.financemanager.investments.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.investments.entities.InvestmentOrderBy;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.finance.financemanager.investments.payloads.*;
import org.finance.financemanager.investments.services.InvestmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ListResponseDto<InvestmentResponseDto> getUsersInvestments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) InvestmentOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection,
            @RequestParam(required = false)InvestmentType type
    ){
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsersInvestments(pageable, query, type);
    }

    @GetMapping("/specific")
    public InvestmentResponseDto getInvestmentById(@RequestParam String investmentId) {
        return service.getInvestmentById(investmentId);
    }

    @PostMapping("/")
    public InvestmentResponseDto createInvestment(@RequestBody InvestmentRequestDto investmentRequest) {
        return service.createInvestment(investmentRequest);
    }

    @PatchMapping("/")
    public InvestmentResponseDto updateInvestment(@RequestParam String investmentId , @RequestBody InvestmentRequestDto investmentRequest) {
        return service.updateInvestment(investmentId, investmentRequest);
    }

    @DeleteMapping("/")
    public SuccessResponseDto deleteInvestment(@RequestParam String investmentId) {
        return service.deleteInvestment(investmentId);
    }

    @PatchMapping("/edit-values/")
    public InvestmentValueResponseDto editInvestmentValue(@RequestParam String investmentId , @RequestBody InvestmentValueRequestDto investmentValueRequest) {
        return service.editInvestmentValue(investmentId, investmentValueRequest);
    }
}
