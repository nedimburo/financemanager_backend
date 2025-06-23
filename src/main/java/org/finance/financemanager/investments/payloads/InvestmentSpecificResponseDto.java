package org.finance.financemanager.investments.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.files.payloads.FileResponseDto;
import org.finance.financemanager.investments.entities.InvestmentType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentSpecificResponseDto {
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
    private String updatedDate;
    private List<FileResponseDto> files;
}
