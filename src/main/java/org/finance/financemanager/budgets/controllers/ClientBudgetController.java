package org.finance.financemanager.budgets.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.budgets.entities.BudgetOrderBy;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.payloads.BudgetSpecificResponseDto;
import org.finance.financemanager.budgets.services.BudgetService;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Operation(
            description = "Fetch paginated results of all budgets made by the user."
    )
    @GetMapping("/")
    public ListResponseDto<BudgetResponseDto> getUsersBudgets(
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

    @Operation(
            description = "Get details for a specific budget made by a user by providing a budget ID."
    )
    @GetMapping("/specific")
    public BudgetSpecificResponseDto getBudgetById(@RequestParam String budgetId) {
        return service.getBudgetById(budgetId);
    }

    @Operation(
            description = "This endpoint is used for creating and storing a new budget."
    )
    @PostMapping("/")
    public BudgetResponseDto createBudget(@RequestBody BudgetRequestDto budgetRequest) {
        return service.createBudget(budgetRequest);
    }

    @Operation(
            description = "Update an existing budget by providing budget ID alongside the new form data."
    )
    @PatchMapping("/")
    public BudgetResponseDto updateBudget(@RequestParam String budgetId , @RequestBody BudgetRequestDto budgetRequest) {
        return service.updateBudget(budgetId, budgetRequest);
    }

    @Operation(
            description = "Delete a specific budget made by a user by providing a budget ID."
    )
    @DeleteMapping("/")
    public SuccessResponseDto deleteBudget(@RequestParam String budgetId) {
        return service.deleteBudget(budgetId);
    }

}
