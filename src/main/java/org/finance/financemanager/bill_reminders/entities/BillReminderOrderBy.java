package org.finance.financemanager.bill_reminders.entities;

import lombok.Getter;

@Getter
public enum BillReminderOrderBy {
    ID("id"),
    BILL_NAME("billName"),
    AMOUNT("amount"),
    RECEIVED_DATE("receivedDate"),
    DUE_DATE("dueDate"),
    IS_PAID("isPaid");

    private final String column;

    BillReminderOrderBy(String column) { this.column = column; }
}
