package org.finance.financemanager.transactions.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.transactions.entities.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    private TransactionType type;
    private FinanceCategory category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
}
