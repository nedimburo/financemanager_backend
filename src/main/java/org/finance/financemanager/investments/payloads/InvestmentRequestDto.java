package org.finance.financemanager.investments.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.investments.entities.InvestmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequestDto {
    private InvestmentType type;
    private String investmentName;
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private Double interestRate;
    private LocalDateTime startDate;
}
