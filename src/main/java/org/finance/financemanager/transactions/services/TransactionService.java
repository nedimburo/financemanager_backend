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
import org.finance.financemanager.common.exceptions.BadRequestException;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.PaginationResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.entities.TransactionType;
import org.finance.financemanager.transactions.mappers.TransactionMapper;
import org.finance.financemanager.transactions.payloads.ExpenseIncomeResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionSpecificResponseDto;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.finance.financemanager.transactions.specifications.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.finance.financemanager.common.enums.FinanceCategory.INVESTMENT;
import static org.finance.financemanager.common.enums.FinanceCategory.UTILITIES;
import static org.finance.financemanager.transactions.entities.TransactionType.EXPENSE;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper transactionMapper;
    private final UserService userService;

    @Transactional
    public ListResponseDto<TransactionResponseDto> getUsersTransactions(Pageable pageable, String query, TransactionType type, FinanceCategory category) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        try {
            Specification<TransactionEntity> spec = TransactionSpecification.filterTransactions(query, type, category, userId);
            Page<TransactionResponseDto> transactionPage = repository.findAll(spec, pageable).map(transactionMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) transactionPage.getTotalElements(),
                    transactionPage.getNumber(),
                    transactionPage.getTotalPages()
            );

            return new ListResponseDto<>(transactionPage.getContent(), paging);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users transactions: ", e);
        }
    }

    @Transactional
    public TransactionSpecificResponseDto getTransactionById(String transactionId) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        UUID transactionUuid;
        try {
            transactionUuid = UUID.fromString(transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting transaction id to UUID.");
        }


        TransactionEntity transaction;
        try {
            transaction = getTransaction(transactionUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Transaction with id: " + transactionId + " doesn't exist");
        }

        try {
            return transactionMapper.toSpecificDto(transaction);
        } catch (Exception e){
            throw new RuntimeException("Error getting transaction by id: " + transactionId, e);
        }
    }

    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto transactionRequest) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        UserEntity user = userService.getUser(userId);

        try {
            TransactionEntity newTransaction = transactionMapper.toEntity(transactionRequest);
            newTransaction.setUser(user);
            TransactionEntity savedTransaction = repository.save(newTransaction);

            TransactionResponseDto response = transactionMapper.toDto(savedTransaction);
            response.setMessage("Transaction has been successfully created");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating transaction: ", e);
        }
    }

    @Transactional
    public TransactionResponseDto updateTransaction(String transactionId, TransactionRequestDto transactionRequest) {
        UUID transactionUuid;
        try {
            transactionUuid = UUID.fromString(transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting transaction id to UUID.");
        }


        TransactionEntity updatedTransaction;
        try {
            updatedTransaction = getTransaction(transactionUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Transaction with id: " + transactionId + " doesn't exist");
        }

        try {
            if (transactionRequest.getType() != null) { updatedTransaction.setType(transactionRequest.getType()); }
            if (transactionRequest.getCategory() != null) { updatedTransaction.setCategory(transactionRequest.getCategory()); }
            if (transactionRequest.getAmount() != null) { updatedTransaction.setAmount(transactionRequest.getAmount()); }
            if (transactionRequest.getDescription() != null) { updatedTransaction.setDescription(transactionRequest.getDescription()); }
            if (transactionRequest.getDate() != null) { updatedTransaction.setDate(transactionRequest.getDate()); }
            repository.save(updatedTransaction);

            TransactionResponseDto response = transactionMapper.toDto(updatedTransaction);
            response.setMessage("Transaction has been successfully updated");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error updating transaction: ", e);
        }
    }

    @Transactional
    public SuccessResponseDto deleteTransaction(String transactionId) {
        UUID transactionUuid;
        try {
            transactionUuid = UUID.fromString(transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting transaction id to UUID.");
        }


        TransactionEntity transaction;
        try {
            transaction = getTransaction(transactionUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Transaction with id: " + transactionId + " doesn't exist");
        }

        try {
            repository.delete(transaction);

            return SuccessResponseDto.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Transaction has been successfully deleted")
                        .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                        .build();
        } catch (Exception e){
            throw new RuntimeException("Error deleting transaction: " + transactionId, e);
        }
    }

    @Transactional
    public List<TransactionResponseDto> getFilteredTransactions(Integer month, Integer year, LocalDate startDate, LocalDate endDate) {
        try {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date must be before end date.");
            }

            String userId;
            try {
                userId = Auth.getUserId();
            } catch (Exception e) {
                throw new UnauthorizedException(e.getMessage());
            }
            List<TransactionEntity> filteredTransactions;

            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                filteredTransactions = repository.findByUserIdAndDateBetween(userId, startDateTime, endDateTime);
            }
            else if (month != null && year != null) {
                filteredTransactions = repository.findByMonthAndYearAndByUserId(userId, month, year);
            } else if (month != null) {
                filteredTransactions = repository.findByMonthAndByUserId(userId, month);
            } else if (year != null) {
                filteredTransactions = repository.findByYearAndByUserId(userId, year);
            } else {
                filteredTransactions = repository.findAllByUserId(userId);
            }
            return filteredTransactions.stream()
                    .map(transactionMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error getting filtered transactions: ", e);
        }
    }

    @Transactional
    public ListResponseDto<TransactionResponseDto> getFilteredTransactionsPageable(Integer month, Integer year, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date must be before end date.");
            }

            String userId;
            try {
                userId = Auth.getUserId();
            } catch (Exception e) {
                throw new UnauthorizedException(e.getMessage());
            }
            Page<TransactionEntity> filteredTransactions;

            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                filteredTransactions = repository.findByUserIdAndDateBetween(userId, startDateTime, endDateTime, pageable);
            }
            else if (month != null && year != null) {
                filteredTransactions = repository.findByMonthAndYearAndByUserIdPageable(userId, month, year, pageable);
            } else if (month != null) {
                filteredTransactions = repository.findByMonthAndByUserIdPageable(userId, month, pageable);
            } else if (year != null) {
                filteredTransactions = repository.findByYearAndByUserIdPageable(userId, year, pageable);
            } else {
                filteredTransactions = repository.findAllByUserId(userId, pageable);
            }
            Page<TransactionResponseDto> transactionsPage = filteredTransactions.map(transactionMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) transactionsPage.getTotalElements(),
                    transactionsPage.getNumber(),
                    transactionsPage.getTotalPages()
            );

            return new ListResponseDto<>(transactionsPage.getContent(), paging);
        } catch (Exception e) {
            throw new RuntimeException("Error getting filtered transactions: ", e);
        }
    }

    @Transactional
    public ExpenseIncomeResponseDto getTotalExpenseAndIncomeForYear(Integer year) {
        try {
            String uid = Auth.getUserId();
            BigDecimal expenseTotal = repository.findTotalExpenseByYearAndUserId(year, uid);
            BigDecimal incomeTotal = repository.findTotalIncomeByYearAndUserId(year, uid);
            return new ExpenseIncomeResponseDto(expenseTotal, incomeTotal);
        } catch (Exception e) {
            throw new RuntimeException("Error getting expense and income for year: " + year, e);
        }
    }

    @Transactional
    public Map<String, ExpenseIncomeResponseDto> getTotalAmountsForEachMonth(Integer year) {
        try {
            String uid = Auth.getUserId();
            Map<String, ExpenseIncomeResponseDto> response = new HashMap<>();
            String[] monthNames = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
            for(int month = 1; month <= 12; month++) {
                BigDecimal totalExpense = repository.findTotalExpenseByYearAndMonthAndUserId(year, month, uid);
                BigDecimal totalIncome = repository.findTotalIncomeByYearAndMonthAndUserId(year, month, uid);
                if (totalExpense == null) {
                    totalExpense = BigDecimal.ZERO;
                }
                if (totalIncome == null) {
                    totalIncome = BigDecimal.ZERO;
                }
                ExpenseIncomeResponseDto data = new ExpenseIncomeResponseDto(totalExpense, totalIncome);
                response.put(monthNames[month - 1], data);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error getting expense and income for each month: " + year, e);
        }
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
            newTransaction.setUser(investment.getUser());
            repository.save(newTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Error creating investment transaction: ", e);
        }
    }

    public TransactionEntity getTransaction(UUID transactionId) {
        return repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));
    }
}
