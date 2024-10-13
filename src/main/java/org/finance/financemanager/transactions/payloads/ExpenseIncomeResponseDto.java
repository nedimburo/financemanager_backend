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
public class ExpenseIncomeResponseDto {
    private BigDecimal totalExpense;
    private BigDecimal totalIncome;
}
