package org.finance.financemanager.savings.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAmountResponseDto {
    private BigDecimal currentAmount;
    private String message;
    private String updatedDate;
}
