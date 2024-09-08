package org.finance.financemanager.bill_reminders.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillReminderRequestDto {
    private String billName;
    private BigDecimal amount;
    private LocalDateTime receivedDate;
    private LocalDateTime dueDate;
}
