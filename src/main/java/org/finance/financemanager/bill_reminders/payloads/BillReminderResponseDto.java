package org.finance.financemanager.bill_reminders.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillReminderResponseDto {
    private String billReminderId;
    private String userId;
    private String billName;
    private BigDecimal amount;
    private String receivedDate;
    private String dueDate;
    private Boolean isPaid;
    private String message;
    private String createdDate;
}
