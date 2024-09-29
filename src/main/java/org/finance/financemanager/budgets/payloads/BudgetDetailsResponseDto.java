package org.finance.financemanager.budgets.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDetailsResponseDto {

    private String biggestBudgetName;
    private BigDecimal biggestBudgetAmount;
    private String lowestBudgetName;
    private BigDecimal lowestBudgetAmount;
}
