package org.finance.financemanager.budgets.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.services.BudgetService;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
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
@RequestMapping("client/budgets")
@Tags(value = {@Tag(name = "Client | Budgets"), @Tag(name = OPERATION_ID_NAME + "ClientBudgets")})
public class ClientBudgetController {

    private final BudgetService service;

    @GetMapping("/")
    public Page<BudgetResponseDto> getUsersBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getUsersBudgets(pageable);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> getBudgetById(@PathVariable String budgetId) {
        return service.getBudgetById(budgetId);
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody BudgetRequestDto budgetRequest) {
        return service.createBudget(budgetRequest);
    }

    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable String budgetId , @RequestBody BudgetRequestDto budgetRequest) {
        return service.updateBudget(budgetId, budgetRequest);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<DeleteResponseDto> deleteBudget(@PathVariable String budgetId) {
        return service.deleteBudget(budgetId);
    }
}
