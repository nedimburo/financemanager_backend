package org.finance.financemanager.investments.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequestDto {
    private String type;
    private String investmentName;
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private Double interestRate;
    private LocalDateTime startDate;
}
