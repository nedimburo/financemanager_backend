package org.finance.financemanager.transactions.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetailsResponseDto {
    private BigDecimal expenseAmount;
    private BigDecimal incomeAmount;
    private Long noOfExpenses;
    private Long noOfIncomes;
    private BigDecimal highestExpenseAmount;
    private BigDecimal highestIncomeAmount;
}
