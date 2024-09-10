package org.finance.financemanager.accessibility.users.repositories;

import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Page<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
