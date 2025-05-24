package org.finance.financemanager.accessibility.users.controllers;

import com.google.firebase.auth.UserRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.auth.payloads.FirebaseUpdateResponseDto;
import org.finance.financemanager.accessibility.users.entities.UserOrderBy;
import org.finance.financemanager.accessibility.users.payloads.UserResponseDto;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Operation(
            description = "Get details for a specific user by providing a user ID."
    )
    @GetMapping("/specific")
    public UserResponseDto getUserById(@RequestParam String userId) {
        return service.getUserById(userId);
    }

    @Operation(
            description = "Fetch paginated results of all available users."
    )
    @GetMapping("/")
    public ListResponseDto<UserResponseDto> getUsers(
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

    @Operation(
            description = "Fetch Firebase user by providing users email."
    )
    @GetMapping("/firebase-user")
    public UserRecord getFirebaseUserByEmail(@RequestParam String email) {
        return service.getFirebaseUserByEmail(email);
    }

    @Operation(
            description = "Update Firebase user profile information."
    )
    @PatchMapping("/update-firebase-user")
    public FirebaseUpdateResponseDto updateFirebaseUser(
            @RequestParam String uid,
            @RequestParam(required = false) String newFirstName,
            @RequestParam(required = false) String newLastName,
            @RequestParam(required = false) String newEmail,
            @RequestParam(required = false) String newPassword
    ) throws Exception {
        return service.updateFirebaseUser(uid, newFirstName, newLastName, newEmail, newPassword);
    }

    @Operation(
            description = "Delete a specific user by providing user ID. Also deletes the user from Firebase."
    )
    @DeleteMapping("/")
    public SuccessResponseDto deleteUser(@RequestParam String userId){
        return service.deleteUser(userId);
    }
}
