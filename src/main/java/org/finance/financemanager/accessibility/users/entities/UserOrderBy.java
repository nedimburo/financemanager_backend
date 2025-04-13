package org.finance.financemanager.accessibility.users.entities;

import lombok.Getter;

@Getter
public enum UserOrderBy {
    ID("id"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    CREATED("created");

    private final String column;

    UserOrderBy(String column) { this.column = column; }
}
