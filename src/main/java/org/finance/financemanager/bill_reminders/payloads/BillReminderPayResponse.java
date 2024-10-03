package org.finance.financemanager.bill_reminders.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillReminderPayResponse {
    private Boolean paymentStatus;
    private String message;
    private String updatedDate;
}
