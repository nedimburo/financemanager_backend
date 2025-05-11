package org.finance.financemanager.savings.entities;

import lombok.Getter;

@Getter
public enum SavingOrderBy {
    ID("id"),
    GOAL_NAME("goalName"),
    TARGET_AMOUNT("targetAmount"),
    CURRENT_AMOUNT("currentAmount"),
    START_DATE("startDate"),
    TARGET_DATE("targetDate");

    private final String column;

    SavingOrderBy(String column) { this.column = column; }
}
