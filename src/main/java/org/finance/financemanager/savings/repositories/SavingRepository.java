package org.finance.financemanager.savings.repositories;

import org.finance.financemanager.savings.entities.SavingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingRepository extends JpaRepository<SavingEntity, String> {
    Page<SavingEntity> findAllByUserId(String userId, Pageable pageable);
    Page<SavingEntity> findAllByUserIdAndGoalNameContainingIgnoreCase(String userId, String goalName, Pageable pageable);
}
