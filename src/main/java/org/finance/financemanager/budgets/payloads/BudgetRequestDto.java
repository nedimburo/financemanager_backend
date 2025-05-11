package org.finance.financemanager.budgets.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.common.enums.FinanceCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDto {
    private String budgetName;
    private FinanceCategory category;
    private BigDecimal budgetLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
