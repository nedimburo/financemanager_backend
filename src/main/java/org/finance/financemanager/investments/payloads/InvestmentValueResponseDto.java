package org.finance.financemanager.investments.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentValueResponseDto {
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private Double interestRate;
    private String message;
    private String updatedDate;
}
