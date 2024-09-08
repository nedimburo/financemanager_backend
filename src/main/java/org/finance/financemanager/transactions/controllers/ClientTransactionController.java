package org.finance.financemanager.transactions.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
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
@RequestMapping("client/transactions")
@Tags(value = {@Tag(name = "Client | Transactions"), @Tag(name = OPERATION_ID_NAME + "ClientTransactions")})
public class ClientTransactionController {

    private final TransactionService service;

    @GetMapping("/")
    public Page<TransactionResponseDto> getUsersTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getUsersTransactions(pageable);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable String transactionId) {
        return service.getTransactionById(transactionId);
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody TransactionRequestDto transactionRequest) {
        return service.createTransaction(transactionRequest);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(@PathVariable String transactionId , @RequestBody TransactionRequestDto transactionRequest) {
        return service.updateTransaction(transactionId, transactionRequest);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<DeleteResponseDto> deleteTransaction(@PathVariable String transactionId) {
        return service.deleteTransaction(transactionId);
    }
}
