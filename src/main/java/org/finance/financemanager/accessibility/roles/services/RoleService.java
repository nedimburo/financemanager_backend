package org.finance.financemanager.accessibility.roles.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.roles.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public RoleEntity findByName(RoleName role) { return repository.findByName(role); }
}
