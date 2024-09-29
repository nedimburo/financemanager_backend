package org.finance.financemanager.transactions.repositories;

import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Page<TransactionEntity> findAllByUserId(String userId, Pageable pageable);
    Page<TransactionEntity> findAllByUserIdAndDescriptionContainingIgnoreCase(String userId, String description, Pageable pageable);
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.type = 'EXPENSE' AND t.user.id = :userId")
    BigDecimal findTotalExpenseByUserId(@Param("userId") String userId);
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.type = 'INCOME' AND t.user.id = :userId")
    BigDecimal findTotalIncomeByUserId(@Param("userId") String userId);
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.type = 'EXPENSE' AND t.user.id = :userId")
    Long countExpensesByUserId(@Param("userId") String userId);
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.type = 'INCOME' AND t.user.id = :userId")
    Long countIncomeByUserId(@Param("userId") String userId);
    @Query("SELECT MAX(t.amount) FROM TransactionEntity t WHERE t.type = 'EXPENSE' AND t.user.id = :userId ")
    BigDecimal findMaxExpenseByUserId(@Param("userId") String userId);
    @Query("SELECT MAX(t.amount) FROM TransactionEntity t WHERE t.type = 'INCOME' AND t.user.id = :userId ")
    BigDecimal findMaxIncomeByUserId(@Param("userId") String userId);
}
