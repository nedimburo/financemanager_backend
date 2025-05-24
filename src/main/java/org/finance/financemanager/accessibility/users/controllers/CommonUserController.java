package org.finance.financemanager.accessibility.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.payloads.FinanceOverviewResponseDto;
import org.finance.financemanager.accessibility.users.payloads.UserProfileResponseDto;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("common/users")
@Tags(value = {@Tag(name = "Common | Users"), @Tag(name = OPERATION_ID_NAME + "CommonUsers")})
public class CommonUserController {

    private final UserService service;

    @Operation(
            description = "Get details for a currently logged in user."
    )
    @GetMapping("/profile")
    public UserProfileResponseDto getUserProfile() throws Exception {
        return service.getUserProfile();
    }

    @Operation(
            description = "Endpoint used for retrieving number of finances that user is currently managing."
    )
    @GetMapping("/financial-overview-numbers")
    public FinanceOverviewResponseDto getFinancialOverviewNumbers() {
        return service.getFinancialOverviewNumbers();
    }
}
