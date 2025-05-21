package org.finance.financemanager.accessibility.users.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.auth.payloads.FirebaseUpdateResponseDto;
import org.finance.financemanager.accessibility.auth.services.FirebaseAuthService;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.roles.services.RoleService;
import org.finance.financemanager.accessibility.users.mappers.UserMapper;
import org.finance.financemanager.accessibility.users.payloads.*;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.repositories.UserRepository;
import org.finance.financemanager.accessibility.users.specifications.UserSpecification;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.exceptions.*;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.PaginationResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

import static org.finance.financemanager.accessibility.roles.entities.RoleName.CLIENT;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final FirebaseAuthService firebaseAuthService;

    @Transactional
    public RoleName getUserRoleById(String id){
        UserEntity user = getUser(id);
        RoleEntity role = user.getRole();
        return role.getName();
    }

    @Transactional
    public ResponseEntity<RegistrationResponseDto> register(HttpServletRequest request, RegistrationRequestDto registrationRequest) throws Exception {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Token header missing or not properly formatted.");
        }

        String token = tokenHeader.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String userId = decodedToken.getUid();

            if (repository.existsById(userId)) {
                throw new ResourceAlreadyExistsException("User profile with ID: " + userId + " already exists.");
            }

            UserEntity newUser = new UserEntity();
            newUser.setId(userId);
            newUser.setEmail(decodedToken.getEmail());
            newUser.setFirstName(registrationRequest.getFirstName());
            newUser.setLastName(registrationRequest.getLastName());
            newUser.setRole(roleService.findByName(CLIENT));
            UserEntity savedUser = repository.save(newUser);

            RegistrationResponseDto response = new RegistrationResponseDto();
            response.setEmail(savedUser.getEmail());
            response.setFirstName(savedUser.getFirstName());
            response.setLastName(savedUser.getLastName());
            response.setRole(savedUser.getRole().getName());
            response.setRegistrationDate(savedUser.getCreated().toString());
            response.setMessage("User has been successfully registered.");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        } catch (FirebaseAuthException e) {
            throw new Exception("Invalid or expired token: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<UserProfileResponseDto> getUserProfile() throws Exception {
        String uid;
        try {
           uid = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        UserEntity user;
        try {
            user = getUser(uid);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("User with ID: " + uid + " doesn't exist");
        }

        try {
            UserProfileResponseDto responseDto = new UserProfileResponseDto();
            responseDto.setUserId(user.getId());
            responseDto.setEmail(user.getEmail());
            responseDto.setFirstName(user.getFirstName());
            responseDto.setLastName(user.getLastName());
            responseDto.setRole(user.getRole().getName());
            responseDto.setRegistrationDate(user.getCreated().toString());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseDto);
        } catch (Exception e) {
            throw new Exception("Error while getting user profile data: ", e);
        }
    }

    @Transactional
    public UserResponseDto getUserById(String userId) {
        UserEntity user;
        try {
            user = getUser(userId);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        try {
            return userMapper.toDto(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting user: ", e);
        }
    }

    @Transactional
    public ListResponseDto<UserResponseDto> getUsers(Pageable pageable, String query){
        try {
            Specification<UserEntity> spec = UserSpecification.filterUsers(query);
            Page<UserResponseDto> usersPage = repository.findAll(spec, pageable).map(userMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) usersPage.getTotalElements(),
                    usersPage.getNumber(),
                    usersPage.getTotalPages()
            );

            return new ListResponseDto<>(usersPage.getContent(), paging);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users: ", e);
        }
    }

    @Transactional
    public UserRecord getFirebaseUserByEmail(String email) {
        try {
            return firebaseAuthService.getUserByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("User not found by the provided email", e);
        }
    }

    @Transactional
    public ResponseEntity<FirebaseUpdateResponseDto> updateFirebaseUser(String uid, String newFirstName, String newLastName, String newEmail, String newPassword) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid);
            UserEntity updatedUser = getUser(uid);
            boolean isUpdated = false;
            if (newFirstName != null && !newEmail.isEmpty()) {
                updatedUser.setFirstName(newFirstName);
                isUpdated = true;
            }
            if (newLastName != null && !newEmail.isEmpty()) {
                updatedUser.setLastName(newLastName);
                isUpdated = true;
            }
            if (isUpdated){
                request.setDisplayName(updatedUser.getFirstName() + " " + updatedUser.getLastName());
            }
            if (newEmail != null && !newEmail.isEmpty()) {
                updatedUser.setEmail(newEmail);
                isUpdated = true;
                request.setEmail(newEmail);
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                request.setPassword(newPassword);
            }
            UserRecord userRecord = firebaseAuthService.updateUser(request);
            if (isUpdated) {
                updatedUser.setUpdated(LocalDateTime.now());
                repository.save(updatedUser);
            }
            FirebaseUpdateResponseDto response = new FirebaseUpdateResponseDto();
            response.setUserId(uid);
            response.setNewFirstName(newFirstName);
            response.setNewLastName(newLastName);
            response.setNewEmail(newEmail);
            response.setPasswordUpdated(newPassword != null);
            response.setMessage("User has been successfully updated.");
            response.setUpdatedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            FirebaseUpdateResponseDto response = new FirebaseUpdateResponseDto();
            response.setMessage("Error updating user.");
            response.setUpdatedDate(LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @Transactional
    public ResponseEntity<SuccessResponseDto> deleteUser(String userId) {
        try {
            UserEntity user = getUser(userId);
            firebaseAuthService.deleteUser(user.getId());
            repository.deleteById(user.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(SuccessResponseDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.CREATED.value())
                            .message("User has been deleted successfully.")
                            .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting user: ", e);
        }
    }

    @Transactional
    public ResponseEntity<FinanceOverviewResponseDto> getFinancialOverviewNumbers(){
        try {
            String uid = Auth.getUserId();
            UserEntity user = getUser(uid);
            FinanceOverviewResponseDto response = new FinanceOverviewResponseDto();
            response.setUserId(user.getId());
            response.setNoOfTransactions(user.getTransactions().size());
            response.setNoOfBillReminders(user.getBillReminders().size());
            response.setNoOfSavings(user.getSavings().size());
            response.setNoOfInvestments(user.getInvestments().size());
            response.setNoOfBudgets(user.getBudgets().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("User not found by the provided uid", e);
        }
    }

    public UserEntity getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public Boolean doesUserExist(String userId) {
        return repository.existsById(userId);
    }
}
