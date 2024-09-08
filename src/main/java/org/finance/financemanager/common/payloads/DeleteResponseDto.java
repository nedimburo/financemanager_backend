package org.finance.financemanager.common.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteResponseDto {
    private String id;
    private String message;
    private String removedDate;
}
