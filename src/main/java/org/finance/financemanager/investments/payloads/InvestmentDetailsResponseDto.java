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
public class InvestmentDetailsResponseDto {

    private BigDecimal totalAmountInvested;
    private BigDecimal totalCurrentValue;
    private String investmentHighInvestedName;
    private BigDecimal investmentHighInvestedAmount;
    private BigDecimal investmentHighInvestedValue;
    private String investmentHighCurrentName;
    private BigDecimal investmentHighCurrentAmount;
    private BigDecimal investmentHighCurrentValue;
}
