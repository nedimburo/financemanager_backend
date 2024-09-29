package org.finance.financemanager.transactions.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.entities.TransactionType;
import org.finance.financemanager.transactions.payloads.TransactionDetailsResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.finance.financemanager.common.enums.FinanceCategory.INVESTMENT;
import static org.finance.financemanager.common.enums.FinanceCategory.UTILITIES;
import static org.finance.financemanager.transactions.entities.TransactionType.EXPENSE;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final UserService userService;

    @Transactional
    public Page<TransactionResponseDto> getUsersTransactions(Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserId(uid, pageable)
                    .map(transaction -> new TransactionResponseDto(
                            transaction.getId(),
                            transaction.getUser().getId(),
                            transaction.getType().toString(),
                            transaction.getCategory().toString(),
                            transaction.getAmount(),
                            transaction.getDescription(),
                            transaction.getDate().toString(),
                            null,
                            transaction.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users transactions: ", e);
        }
    }

    @Transactional
    public Page<TransactionResponseDto> searchUsersTransactions(String description, Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserIdAndDescriptionContainingIgnoreCase(uid, description, pageable)
                    .map(transaction -> new TransactionResponseDto(
                            transaction.getId(),
                            transaction.getUser().getId(),
                            transaction.getType().toString(),
                            transaction.getCategory().toString(),
                            transaction.getAmount(),
                            transaction.getDescription(),
                            transaction.getDate().toString(),
                            null,
                            transaction.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users transactions: ", e);
        }
    }

    @Transactional
    public ResponseEntity<TransactionResponseDto> getTransactionById(String transactionId) {
        try {
            TransactionEntity transaction = getTransaction(transactionId);
            TransactionResponseDto response = formatTransactionResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error getting transaction by id: " + transactionId, e);
        }
    }

    @Transactional
    public ResponseEntity<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest) {
        try {
            String uid = Auth.getUserId();
            UserEntity user = userService.getUser(uid);

            TransactionEntity newTransaction = new TransactionEntity();
            newTransaction.setId(UUID.randomUUID().toString());
            newTransaction.setType(TransactionType.valueOf(transactionRequest.getType()));
            newTransaction.setCategory(FinanceCategory.valueOf(transactionRequest.getCategory()));
            newTransaction.setAmount(transactionRequest.getAmount());
            newTransaction.setDescription(transactionRequest.getDescription());
            newTransaction.setDate(transactionRequest.getDate());
            newTransaction.setCreated(LocalDateTime.now());
            newTransaction.setUpdated(LocalDateTime.now());
            newTransaction.setUser(user);
            repository.save(newTransaction);

            TransactionResponseDto response = formatTransactionResponse(newTransaction);
            response.setMessage("Transaction has been successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating transaction: ", e);
        }
    }

    @Transactional
    public ResponseEntity<TransactionResponseDto> updateTransaction(String transactionId, TransactionRequestDto transactionRequest) {
        try {
            TransactionEntity updatedTransaction = getTransaction(transactionId);
            if (transactionRequest.getType() != null) { updatedTransaction.setType(TransactionType.valueOf(transactionRequest.getType())); }
            if (transactionRequest.getCategory() != null) { updatedTransaction.setCategory(FinanceCategory.valueOf(transactionRequest.getCategory())); }
            if (transactionRequest.getAmount() != null) { updatedTransaction.setAmount(transactionRequest.getAmount()); }
            if (transactionRequest.getDescription() != null) { updatedTransaction.setDescription(transactionRequest.getDescription()); }
            if (transactionRequest.getDate() != null) { updatedTransaction.setDate(transactionRequest.getDate()); }
            updatedTransaction.setUpdated(LocalDateTime.now());
            repository.save(updatedTransaction);

            TransactionResponseDto response = formatTransactionResponse(updatedTransaction);
            response.setMessage("Transaction has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating transaction: ", e);
        }
    }

    @Transactional
    public ResponseEntity<DeleteResponseDto> deleteTransaction(String transactionId) {
        try {
            TransactionEntity transaction = getTransaction(transactionId);
            repository.delete(transaction);

            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(transactionId);
            response.setMessage("Transaction has been successfully deleted");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error deleting transaction: " + transactionId, e);
        }
    }

    @Transactional
    public ResponseEntity<TransactionDetailsResponseDto> getTransactionDetails() {
        try {
            String uid = Auth.getUserId();
            BigDecimal totalExpense = repository.findTotalExpenseByUserId(uid);
            BigDecimal totalIncome = repository.findTotalIncomeByUserId(uid);
            Long noOfExpense = repository.countExpensesByUserId(uid);
            Long noOfIncome = repository.countIncomeByUserId(uid);
            BigDecimal maxExpenseAmount = repository.findMaxExpenseByUserId(uid);
            BigDecimal maxIncomeAmount = repository.findMaxIncomeByUserId(uid);
            TransactionDetailsResponseDto response = new TransactionDetailsResponseDto();
            response.setExpenseAmount(totalExpense != null ? totalExpense : BigDecimal.ZERO);
            response.setIncomeAmount(totalIncome != null ? totalIncome : BigDecimal.ZERO);
            response.setNoOfExpenses(noOfExpense != null ? noOfExpense : 0);
            response.setNoOfIncomes(noOfIncome != null ? noOfIncome : 0);
            response.setHighestExpenseAmount(maxExpenseAmount != null ? maxExpenseAmount : BigDecimal.ZERO);
            response.setHighestIncomeAmount(maxIncomeAmount != null ? maxIncomeAmount : BigDecimal.ZERO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error getting transaction details: " + e);
        }
    }

    private TransactionResponseDto formatTransactionResponse(TransactionEntity transaction) {
        TransactionResponseDto response = new TransactionResponseDto();
        response.setTransactionId(transaction.getId());
        response.setUserId(transaction.getUser().getId());
        response.setType(transaction.getType().toString());
        response.setCategory(transaction.getCategory().toString());
        response.setAmount(transaction.getAmount() != null ? transaction.getAmount() : BigDecimal.ZERO);
        response.setDescription(transaction.getDescription());
        response.setDate(transaction.getDate().toString());
        response.setCreatedDate(transaction.getCreated().toString());
        return response;
    }

    public void createTransactionFromBillReminder(BillReminderEntity billReminder) {
        try {
            TransactionEntity newTransaction = new TransactionEntity();
            newTransaction.setId(billReminder.getId());
            newTransaction.setType(EXPENSE);
            newTransaction.setCategory(UTILITIES);
            newTransaction.setAmount(billReminder.getAmount());
            newTransaction.setDate(billReminder.getReceivedDate());
            newTransaction.setDescription(billReminder.getBillName());
            newTransaction.setCreated(LocalDateTime.now());
            newTransaction.setUpdated(LocalDateTime.now());
            newTransaction.setUser(billReminder.getUser());
            repository.save(newTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Error creating bill reminder transaction: ", e);
        }
    }

    public void createTransactionFromInvestment(InvestmentEntity investment) {
        try {
            TransactionEntity newTransaction = new TransactionEntity();
            newTransaction.setId(investment.getId());
            newTransaction.setType(EXPENSE);
            newTransaction.setCategory(INVESTMENT);
            newTransaction.setAmount(investment.getAmountInvested());
            newTransaction.setDate(investment.getStartDate());
            newTransaction.setDescription(investment.getInvestmentName());
            newTransaction.setCreated(LocalDateTime.now());
            newTransaction.setUpdated(LocalDateTime.now());
            newTransaction.setUser(investment.getUser());
            repository.save(newTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Error creating investment transaction: ", e);
        }
    }

    public TransactionEntity getTransaction(String transactionId) {
        return repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));
    }
}
