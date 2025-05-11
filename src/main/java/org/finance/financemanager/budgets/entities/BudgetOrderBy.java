package org.finance.financemanager.budgets.entities;

import lombok.Getter;

@Getter
public enum BudgetOrderBy {
    ID("id"),
    BUDGET_NAME("budgetName"),
    BUDGET_LIMIT("budgetLimit"),
    START_DATE("startDate"),
    END_DATE("endDate");

    private final String column;

    BudgetOrderBy(String column) { this.column = column; }
}
