package org.finance.financemanager.accessibility.users.controllers;

import com.google.firebase.auth.UserRecord;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserOrderBy;
import org.finance.financemanager.accessibility.users.payloads.UserResponseDto;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/users")
@Tags(value = {@Tag(name = "Admin | Users"), @Tag(name = OPERATION_ID_NAME + "AdminUsers")})
public class AdminUserController {

    private final UserService service;

    @GetMapping("/specific")
    public UserResponseDto getUserById(@RequestParam String userId) {
        return service.getUserById(userId);
    }

    @GetMapping("/")
    public Page<UserResponseDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UserOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection
    ) {
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsers(pageable, query);
    }

    @GetMapping("/firebase-user")
    public UserRecord getFirebaseUserByEmail(@RequestParam String email) {
        return service.getFirebaseUserByEmail(email);
    }

    @PatchMapping("/update-firebase-user")
    public ResponseEntity<?> updateFirebaseUser(
            @RequestParam String uid,
            @RequestParam(required = false) String newFirstName,
            @RequestParam(required = false) String newLastName,
            @RequestParam(required = false) String newEmail,
            @RequestParam(required = false) String newPassword
    ){
        return service.updateFirebaseUser(uid, newFirstName, newLastName, newEmail, newPassword);
    }

    @DeleteMapping("/")
    public ResponseEntity<SuccessResponseDto> deleteUser(@RequestParam String userId){
        return service.deleteUser(userId);
    }
}
