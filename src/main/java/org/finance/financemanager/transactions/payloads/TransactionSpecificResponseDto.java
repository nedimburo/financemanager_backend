package org.finance.financemanager.transactions.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.files.payloads.FileResponseDto;
import org.finance.financemanager.transactions.entities.TransactionType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSpecificResponseDto {
    private String transactionId;
    private String userId;
    private TransactionType type;
    private FinanceCategory category;
    private BigDecimal amount;
    private String description;
    private String date;
    private String message;
    private String createdDate;
    private String updatedDate;
    private List<FileResponseDto> files;
}
