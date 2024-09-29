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
public class SavingDetailsResponseDto {

    private String closestSavingGoalName;
    private BigDecimal closestSavingCurrentAmount;
    private BigDecimal closestSavingTargetAmount;
    private String furthestSavingGoalName;
    private BigDecimal furthestSavingCurrentAmount;
    private BigDecimal furthestSavingTargetAmount;
}
