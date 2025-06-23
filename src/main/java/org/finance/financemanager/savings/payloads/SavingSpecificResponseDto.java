package org.finance.financemanager.savings.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.files.payloads.FileResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingSpecificResponseDto {
    private String savingId;
    private String userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    private String message;
    private String createdDate;
    private String updatedDate;
    private List<FileResponseDto> files;
}
