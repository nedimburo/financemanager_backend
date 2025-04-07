package org.finance.financemanager.transactions.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.transactions.entities.TransactionOrderBy;
import org.finance.financemanager.transactions.entities.TransactionType;
import org.finance.financemanager.transactions.payloads.ExpenseIncomeResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionDetailsResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/")
    public Page<TransactionResponseDto> getUsersTransactions(
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

    @GetMapping("/specific")
    public TransactionResponseDto getTransactionById(@RequestParam String transactionId) {
        return service.getTransactionById(transactionId);
    }

    @PostMapping("/")
    public TransactionResponseDto createTransaction(@RequestBody TransactionRequestDto transactionRequest) {
        return service.createTransaction(transactionRequest);
    }

    @PatchMapping("/{transactionId}")
    public TransactionResponseDto updateTransaction(@PathVariable String transactionId , @RequestBody TransactionRequestDto transactionRequest) {
        return service.updateTransaction(transactionId, transactionRequest);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<SuccessResponseDto> deleteTransaction(@PathVariable String transactionId) {
        return service.deleteTransaction(transactionId);
    }

    @GetMapping("/details")
    public ResponseEntity<TransactionDetailsResponseDto> getTransactionDetails() {
        return service.getTransactionDetails();
    }

    @GetMapping("/filtered")
    public List<TransactionResponseDto> getFilteredTransactions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return service.getFilteredTransactions(month, year);
    }

    @GetMapping("/filtered-page")
    public Page<TransactionResponseDto> getFilteredTransactionsPageable(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getFilteredTransactionsPageable(month, year, pageable);
    }

    @GetMapping("/total-amounts-yearly")
    public ResponseEntity<ExpenseIncomeResponseDto> getTotalExpenseAndIncomeForYear(
            @RequestParam Integer year
    ) {
        return service.getTotalExpenseAndIncomeForYear(year);
    }

    @GetMapping("/total-amounts-monthly")
    public Map<String, ExpenseIncomeResponseDto> getTotalAmountsForEachMonth(
            @RequestParam Integer year
    ) {
        return service.getTotalAmountsForEachMonth(year);
    }
}
