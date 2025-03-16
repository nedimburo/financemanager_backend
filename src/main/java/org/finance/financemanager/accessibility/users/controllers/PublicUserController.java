package org.finance.financemanager.accessibility.users.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.payloads.RegistrationRequestDto;
import org.finance.financemanager.accessibility.users.payloads.RegistrationResponseDto;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("public/users")
@Tags(value = {@Tag(name = "Public | Users"), @Tag(name = OPERATION_ID_NAME + "PublicUser")})
public class PublicUserController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(HttpServletRequest request, @RequestBody @Valid RegistrationRequestDto registrationRequest) throws Exception {
        return service.register(request, registrationRequest);
    }
}
