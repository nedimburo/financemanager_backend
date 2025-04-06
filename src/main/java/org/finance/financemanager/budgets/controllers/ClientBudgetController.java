package org.finance.financemanager.budgets.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.budgets.entities.BudgetOrderBy;
import org.finance.financemanager.budgets.payloads.BudgetDetailsResponseDto;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.services.BudgetService;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BudgetOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection,
            @RequestParam(required = false) FinanceCategory category
    ){
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsersBudgets(pageable, query, category);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> getBudgetById(@PathVariable String budgetId) {
        return service.getBudgetById(budgetId);
    }

    @PostMapping("/")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody BudgetRequestDto budgetRequest) {
        return service.createBudget(budgetRequest);
    }

    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable String budgetId , @RequestBody BudgetRequestDto budgetRequest) {
        return service.updateBudget(budgetId, budgetRequest);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<SuccessResponseDto> deleteBudget(@PathVariable String budgetId) {
        return service.deleteBudget(budgetId);
    }

    @GetMapping("/details")
    public ResponseEntity<BudgetDetailsResponseDto> getBudgetDetails() {
        return service.getBudgetDetails();
    }
}
