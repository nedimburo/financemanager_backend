package org.finance.financemanager.common.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDto {
    private Integer totalItemsNumber;
    private Integer currentPage;
    private Integer totalPagesNumber;
}
