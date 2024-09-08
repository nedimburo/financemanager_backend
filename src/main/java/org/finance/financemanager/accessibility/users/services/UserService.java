package org.finance.financemanager.accessibility.users.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.roles.services.RoleService;
import org.finance.financemanager.accessibility.users.payloads.RegistrationRequestDto;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.payloads.RegistrationResponseDto;
import org.finance.financemanager.accessibility.users.payloads.UserProfileResponseDto;
import org.finance.financemanager.accessibility.users.repositories.UserRepository;
import org.finance.financemanager.common.config.Auth;
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

    @Transactional
    public RoleName getUserRoleById(String id){
        UserEntity user = getUser(id);
        RoleEntity role = user.getRole();
        return role.getName();
    }

    @Transactional
    @SneakyThrows
    public ResponseEntity<RegistrationResponseDto> register(HttpServletRequest request, RegistrationRequestDto registrationRequest){
        String tokenHeader = request.getHeader("Token");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                UserEntity newUser = new UserEntity();
                newUser.setId(decodedToken.getUid());
                newUser.setEmail(decodedToken.getEmail());
                newUser.setFirstName(registrationRequest.getFirstName());
                newUser.setLastName(registrationRequest.getLastName());
                newUser.setRole(roleService.findByName(CLIENT));
                newUser.setCreated(LocalDateTime.now());
                newUser.setUpdated(LocalDateTime.now());
                repository.save(newUser);

                RegistrationResponseDto response = new RegistrationResponseDto();
                response.setEmail(newUser.getEmail());
                response.setFirstName(newUser.getFirstName());
                response.setLastName(newUser.getLastName());
                response.setRole(newUser.getRole().getName());
                response.setRegistrationDate(newUser.getCreated().toString());
                response.setMessage("User has been successfully registered.");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                throw new RuntimeException("Invalid or expired token", e);
            }
        } else {
            throw new RuntimeException("Token header missing or doesn't contain Bearer token");
        }
    }

    @Transactional
    @SneakyThrows
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {
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
