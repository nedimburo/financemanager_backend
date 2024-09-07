package org.finance.financemanager.budgets.repositories;

import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<BudgetEntity, String> {
}
