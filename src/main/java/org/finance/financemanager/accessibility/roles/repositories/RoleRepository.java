package org.finance.financemanager.accessibility.roles.repositories;

import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    RoleEntity findByName(RoleName name);
}
