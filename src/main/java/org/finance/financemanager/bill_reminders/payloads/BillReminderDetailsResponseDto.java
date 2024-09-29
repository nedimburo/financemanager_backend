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
public class BillReminderDetailsResponseDto {

    private BigDecimal totalAmountPaidBills;
    private BigDecimal totalAmountUnpaidBills;
    private String billToPayName;
    private BigDecimal billToPayAmount;
    private String billToPayDueDate;
}
