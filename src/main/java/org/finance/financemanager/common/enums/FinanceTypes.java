package org.finance.financemanager.common.enums;

import lombok.Getter;

@Getter
public enum FinanceTypes {
    TRANSACTIONS("transactions"),
    BILL_REMINDERS("bill_reminders"),
    INVESTMENTS("investments"),
    BUDGETS("budgets"),
    SAVINGS("savings");

    private final String column;

    FinanceTypes(String column) { this.column = column; }
}
