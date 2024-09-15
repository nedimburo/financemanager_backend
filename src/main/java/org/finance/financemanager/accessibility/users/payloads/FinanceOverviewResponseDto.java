package org.finance.financemanager.accessibility.users.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinanceOverviewResponseDto {
    private String userId;
    private Integer noOfTransactions;
    private Integer noOfBillReminders;
    private Integer noOfSavings;
    private Integer noOfInvestments;
    private Integer noOfBudgets;
}
