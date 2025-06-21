package org.finance.financemanager.transactions.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.transactions.entities.TransactionOrderBy;
import org.finance.financemanager.transactions.entities.TransactionType;
import org.finance.financemanager.transactions.payloads.ExpenseIncomeResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("client/transactions")
@Tags(value = {@Tag(name = "Client | Transactions"), @Tag(name = OPERATION_ID_NAME + "ClientTransactions")})
public class ClientTransactionController {

    private final TransactionService service;

    @Operation(
            description = "Fetch paginated results of all transactions made by the user."
    )
    @GetMapping("/")
    public ListResponseDto<TransactionResponseDto> getUsersTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TransactionOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) FinanceCategory category
    ){
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsersTransactions(pageable, query, type, category);
    }

    @Operation(
            description = "Get details for a specific transaction made by a user by providing a transaction ID."
    )
    @GetMapping("/specific")
    public TransactionResponseDto getTransactionById(@RequestParam String transactionId) {
        return service.getTransactionById(transactionId);
    }

    @Operation(
            description = "This endpoint is used for creating and storing a new transaction."
    )
    @PostMapping("/")
    public TransactionResponseDto createTransaction(@RequestBody TransactionRequestDto transactionRequest) {
        return service.createTransaction(transactionRequest);
    }

    @Operation(
            description = "Update an existing transaction by providing transaction ID alongside the new form data."
    )
    @PatchMapping("/")
    public TransactionResponseDto updateTransaction(@RequestParam String transactionId , @RequestBody TransactionRequestDto transactionRequest) {
        return service.updateTransaction(transactionId, transactionRequest);
    }

    @Operation(
            description = "Delete a specific transaction made by a user by providing a transaction ID."
    )
    @DeleteMapping("/")
    public SuccessResponseDto deleteTransaction(@RequestParam String transactionId) {
        return service.deleteTransaction(transactionId);
    }

    @Operation(
            description = "Get filtered transactions by a specific period without pagination."
    )
    @GetMapping("/filtered")
    public List<TransactionResponseDto> getFilteredTransactions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return service.getFilteredTransactions(month, year, startDate, endDate);
    }

    @Operation(
            description = "Get filtered transactions by a specific period with pagination."
    )
    @GetMapping("/filtered-page")
    public ListResponseDto<TransactionResponseDto> getFilteredTransactionsPageable(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getFilteredTransactionsPageable(month, year, startDate, endDate, pageable);
    }

    @Operation(
            description = "Get total expense and income information for a specific year."
    )
    @GetMapping("/total-amounts-yearly")
    public ExpenseIncomeResponseDto getTotalExpenseAndIncomeForYear(
            @RequestParam Integer year
    ) {
        return service.getTotalExpenseAndIncomeForYear(year);
    }

    @Operation(
            description = "Get total expense and income information for every month in a specific year."
    )
    @GetMapping("/total-amounts-monthly")
    public Map<String, ExpenseIncomeResponseDto> getTotalAmountsForEachMonth(
            @RequestParam Integer year
    ) {
        return service.getTotalAmountsForEachMonth(year);
    }
}
