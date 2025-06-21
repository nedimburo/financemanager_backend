package org.finance.financemanager.common.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class EnumsService {

    public List<String> getFinanceCategories() {
        return Arrays.stream(FinanceCategory.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public List<String> getInvestmentTypes() {
        return Arrays.stream(InvestmentType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
