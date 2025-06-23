package org.finance.financemanager.bill_reminders.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.files.payloads.FileResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillReminderSpecificResponseDto {
    private String billReminderId;
    private String userId;
    private String billName;
    private BigDecimal amount;
    private String receivedDate;
    private String dueDate;
    private Boolean isPaid;
    private String message;
    private String createdDate;
    private String updatedDate;
    private List<FileResponseDto> files;
}
