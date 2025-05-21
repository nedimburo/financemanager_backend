package org.finance.financemanager.savings.repositories;

import org.finance.financemanager.savings.entities.SavingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SavingRepository extends JpaRepository<SavingEntity, UUID>, JpaSpecificationExecutor<SavingEntity> {
    @Query("SELECT s FROM SavingEntity s WHERE s.user.id = :userId AND " +
            "(s.targetAmount - s.currentAmount) = " +
            "(SELECT MIN(s2.targetAmount - s2.currentAmount) FROM SavingEntity s2 WHERE s2.user.id = :userId)")
    SavingEntity findSavingWithSmallestDifferenceByUserId(@Param("userId") String userId);
    @Query("SELECT s FROM SavingEntity s WHERE s.user.id = :userId AND " +
            "(s.targetAmount - s.currentAmount) = " +
            "(SELECT MAX(s2.targetAmount - s2.currentAmount) FROM SavingEntity s2 WHERE s2.user.id = :userId)")
    SavingEntity findSavingWithBiggestDifferenceByUserId(@Param("userId") String userId);
}
