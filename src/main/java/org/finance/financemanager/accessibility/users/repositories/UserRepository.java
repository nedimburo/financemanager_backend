package org.finance.financemanager.accessibility.users.repositories;

import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
