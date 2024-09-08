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
public class TransactionResponseDto {
    private String transactionId;
    private String userId;
    private String type;
    private String category;
    private BigDecimal amount;
    private String description;
    private String date;
    private String message;
    private String createdDate;
}
