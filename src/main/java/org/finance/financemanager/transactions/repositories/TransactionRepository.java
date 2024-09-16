package org.finance.financemanager.transactions.repositories;

import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Page<TransactionEntity> findAllByUserId(String userId, Pageable pageable);
    Page<TransactionEntity> findAllByUserIdAndDescriptionContainingIgnoreCase(String userId, String description, Pageable pageable);
}
