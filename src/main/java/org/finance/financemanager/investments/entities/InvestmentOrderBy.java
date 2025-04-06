package org.finance.financemanager.investments.entities;

import lombok.Getter;

@Getter
public enum InvestmentOrderBy {
    ID("id"),
    INVESTMENT_NAME("investmentName"),
    AMOUNT_INVESTED("amountInvested"),
    CURRENT_VALUE("currentValue"),
    INTEREST_RATE("interestRate"),
    START_DATE("startDate");

    private final String column;

    InvestmentOrderBy(String column) { this.column = column; }
}
