package org.finance.financemanager.transactions.entities;

import lombok.Getter;

@Getter
public enum TransactionOrderBy {
    ID("id"),
    TYPE("type"),
    CATEGORY("category"),
    AMOUNT("amount"),
    DESCRIPTION("description"),
    DATE("date");

    private final String column;

    TransactionOrderBy(String column) {
        this.column = column;
    }
}
