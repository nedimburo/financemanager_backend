package org.finance.financemanager.budgets.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.common.enums.FinanceCategory;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDto {
    private String budgetId;
    private String userId;
    private String budgetName;
    private FinanceCategory category;
    private BigDecimal budgetLimit;
    private String startDate;
    private String endDate;
    private String message;
    private String createdDate;
}
