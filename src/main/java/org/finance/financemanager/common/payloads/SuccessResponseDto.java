package org.finance.financemanager.common.payloads;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SuccessResponseDto {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
}
