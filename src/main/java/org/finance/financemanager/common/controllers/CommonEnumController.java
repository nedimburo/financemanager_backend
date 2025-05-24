package org.finance.financemanager.common.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("public/enums")
@Tags(value = {@Tag(name = "Public | Enums"), @Tag(name = OPERATION_ID_NAME + "PublicEnums")})
public class CommonEnumController {

    @Operation(
            description = "Get all finance categories that are available for management."
    )
    @GetMapping("/finance-categories")
    public List<String> getFinanceCategories() {
        return Arrays.stream(FinanceCategory.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Operation(
            description = "Get all types of investments that user can select for investment finances."
    )
    @GetMapping("/investment-types")
    public List<String> getInvestmentTypes() {
        return Arrays.stream(InvestmentType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
