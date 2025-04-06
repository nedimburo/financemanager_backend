package org.finance.financemanager.budgets.repositories;

import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<BudgetEntity, String>, JpaSpecificationExecutor<BudgetEntity> {
    Page<BudgetEntity> findAllByUserId(String userId, Pageable pageable);
    Page<BudgetEntity> findAllByUserIdAndBudgetNameIsContainingIgnoreCase(String userId, String budgetName, Pageable pageable);
    @Query("SELECT b FROM BudgetEntity b WHERE b.user.id = :userId AND b.budgetLimit = " +
            "(SELECT MAX(b2.budgetLimit) FROM BudgetEntity b2 WHERE b2.user.id = :userId)")
    BudgetEntity findBiggestBudgetByUserId(@Param("userId") String userId);
    @Query("SELECT b FROM BudgetEntity b WHERE b.user.id = :userId AND b.budgetLimit = " +
            "(SELECT MIN(b2.budgetLimit) FROM BudgetEntity b2 WHERE b2.user.id = :userId)")
    BudgetEntity findLowestBudgetByUserId(@Param("userId") String userId);
}
