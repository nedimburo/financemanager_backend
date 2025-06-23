package org.finance.financemanager.budgets.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.files.payloads.FileResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSpecificResponseDto {
    private String budgetId;
    private String userId;
    private String budgetName;
    private FinanceCategory category;
    private BigDecimal budgetLimit;
    private String startDate;
    private String endDate;
    private String message;
    private String createdDate;
    private String updatedDate;
    private List<FileResponseDto> files;
}
