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
import org.finance.financemanager.accessibility.users.payloads.*;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.repositories.UserRepository;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.finance.financemanager.accessibility.roles.entities.RoleName.CLIENT;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleService roleService;
    private final FirebaseAuthService firebaseAuthService;

    @Transactional
    public RoleName getUserRoleById(String id){
        UserEntity user = getUser(id);
        RoleEntity role = user.getRole();
        return role.getName();
    }

    @Transactional
    public ResponseEntity<?> register(HttpServletRequest request, RegistrationRequestDto registrationRequest){
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token header missing or not properly formatted.");
        }

        String token = tokenHeader.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String userId = decodedToken.getUid();

            if (repository.existsById(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User profile already exists.");
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
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

    @Transactional
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {
        try {
            String uid = Auth.getUserId();
            UserEntity user = getUser(uid);
            UserProfileResponseDto profileResponse = new UserProfileResponseDto();
            profileResponse.setUserId(user.getId());
            profileResponse.setEmail(user.getEmail());
            profileResponse.setFirstName(user.getFirstName());
            profileResponse.setLastName(user.getLastName());
            profileResponse.setRole(user.getRole().getName());
            profileResponse.setRegistrationDate(formatedCreatedDate(user.getCreated()));
            return ResponseEntity.ok(profileResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting user profile data: ", e);
        }
    }

    @Transactional
    public ResponseEntity<UserResponseDto> getUserById(String userId) {
        try {
            UserEntity user = getUser(userId);
            UserResponseDto response = new UserResponseDto();
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setRole(user.getRole().getName());
            response.setNumberOfTransactions(user.getTransactions().size());
            response.setRegistrationDate(formatedCreatedDate(user.getCreated()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting user: ", e);
        }
    }

    @Transactional
    public Page<UserResponseDto> getUsers(Pageable pageable){
        return repository.findAll(pageable)
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole().getName(),
                        user.getTransactions().size(),
                        user.getCreated().toString()
                ));
    }

    @Transactional
    public Page<UserResponseDto> searchUsersByEmail(String email, Pageable pageable) {
        return repository.findByEmailContainingIgnoreCase(email, pageable)
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole().getName(),
                        user.getTransactions().size(),
                        user.getCreated().toString()
                ));
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
    public ResponseEntity<DeleteResponseDto> deleteUser(String userId) {
        try {
            UserEntity user = getUser(userId);
            firebaseAuthService.deleteUser(user.getId());
            repository.deleteById(user.getId());
            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(userId);
            response.setMessage("User has been deleted successfully.");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
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

    private String formatedCreatedDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy.");
        return date.format(formatter);
    }

    public UserEntity getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}
