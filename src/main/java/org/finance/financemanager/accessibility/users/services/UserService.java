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
import org.finance.financemanager.accessibility.users.RegistrationRequestDto;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public ResponseEntity<?> register(HttpServletRequest request, RegistrationRequestDto registrationRequest){
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
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
            } catch (Exception e) {
                throw new RuntimeException("Invalid or expired token", e);
            }
        } else {
            throw new RuntimeException("Authorization header missing or doesn't contain Bearer token");
        }
        return new ResponseEntity<>("User has been successfully registered.", HttpStatus.OK);
    }

    public UserEntity getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}
