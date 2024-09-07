package org.finance.financemanager.transactions.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    private TransactionEntity getTransaction(String transactionId) {
        return repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));
    }
}
