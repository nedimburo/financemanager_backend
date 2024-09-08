package org.finance.financemanager.transactions.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDeleteResponseDto {
    private String transactionId;
    private String message;
    private String removedDate;
}
