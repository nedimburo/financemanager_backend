package org.finance.financemanager.transactions.repositories;

import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {
    Page<TransactionEntity> findAllByUserId(String userId, Pageable pageable);
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
    @Query("SELECT t FROM TransactionEntity t WHERE MONTH(t.date) = :month AND YEAR(t.date) = :year AND t.user.id = :userId ORDER BY t.date ASC")
    List<TransactionEntity> findByMonthAndYearAndByUserId(@Param("userId") String userId, @Param("month") Integer month, @Param("year") Integer year);
    @Query("SELECT t FROM TransactionEntity t WHERE MONTH(t.date) = :month AND t.user.id = :userId ORDER BY t.date ASC")
    List<TransactionEntity> findByMonthAndByUserId(@Param("userId") String userId, @Param("month") Integer month);
    @Query("SELECT t FROM TransactionEntity t WHERE YEAR(t.date) = :year AND t.user.id = :userId ORDER BY t.date ASC")
    List<TransactionEntity> findByYearAndByUserId(@Param("userId") String userId, @Param("year") Integer year);
    @Query("SELECT t FROM TransactionEntity t WHERE MONTH(t.date) = :month AND YEAR(t.date) = :year AND t.user.id = :userId ORDER BY t.date ASC")
    Page<TransactionEntity> findByMonthAndYearAndByUserIdPageable(@Param("userId") String userId, @Param("month") Integer month, @Param("year") Integer year, Pageable pageable);
    @Query("SELECT t FROM TransactionEntity t WHERE MONTH(t.date) = :month AND t.user.id = :userId ORDER BY t.date ASC")
    Page<TransactionEntity> findByMonthAndByUserIdPageable(@Param("userId") String userId, @Param("month") Integer month, Pageable pageable);
    @Query("SELECT t FROM TransactionEntity t WHERE YEAR(t.date) = :year AND t.user.id = :userId ORDER BY t.date ASC")
    Page<TransactionEntity> findByYearAndByUserIdPageable(@Param("userId") String userId, @Param("year") Integer year, Pageable pageable);
    @Query("SELECT SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END)" +
            "FROM TransactionEntity t WHERE YEAR(t.date) = :year AND t.user.id = :userId")
    BigDecimal findTotalExpenseByYearAndUserId(@Param("year") Integer year, @Param("userId") String userId);
    @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END)" +
            "FROM TransactionEntity t WHERE YEAR(t.date) = :year AND t.user.id = :userId")
    BigDecimal findTotalIncomeByYearAndUserId(@Param("year") Integer year, @Param("userId") String userId);
    @Query("SELECT SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END)" +
            "FROM TransactionEntity t WHERE YEAR(t.date) = :year AND MONTH(t.date) = :month AND t.user.id = :userId")
    BigDecimal findTotalExpenseByYearAndMonthAndUserId(@Param("year") Integer year, @Param("month") Integer month, @Param("userId") String userId);
    @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END)" +
            "FROM TransactionEntity t WHERE YEAR(t.date) = :year AND MONTH(t.date) = :month AND t.user.id = :userId")
    BigDecimal findTotalIncomeByYearAndMonthAndUserId(@Param("year") Integer year, @Param("month") Integer month, @Param("userId") String userId);

}
