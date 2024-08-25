package org.finance.financemanager.accessibility.users.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    @Transactional
    public RoleName getUserRoleById(String id){
        UserEntity user = getUser(id);
        RoleEntity role = user.getRole();
        return role.getName();
    }

    public UserEntity getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}
