package org.finance.financemanager.investments.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.investments.entities.InvestmentType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponseDto {
    private String investmentId;
    private String userId;
    private InvestmentType type;
    private String investmentName;
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private Double interestRate;
    private String startDate;
    private String message;
    private String createdDate;
}
