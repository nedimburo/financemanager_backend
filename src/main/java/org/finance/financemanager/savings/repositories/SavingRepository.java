package org.finance.financemanager.savings.repositories;

import org.finance.financemanager.savings.entities.SavingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingRepository extends JpaRepository<SavingEntity, String> {
}
