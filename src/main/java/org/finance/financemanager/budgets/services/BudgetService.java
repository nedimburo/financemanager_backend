package org.finance.financemanager.budgets.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository repository;

    private BudgetEntity getBudget(String budgetId) {
        return repository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + budgetId));
    }
}
