package org.finance.financemanager.files.repositories;

import org.finance.financemanager.files.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    List<FileEntity> findByTransactionId(UUID transactionId);
    List<FileEntity> findByBillReminderId(UUID billReminderId);
    List<FileEntity> findByInvestmentId(UUID investmentId);
    List<FileEntity> findByBudgetId(UUID budgetId);
    List<FileEntity> findBySavingId(UUID savingId);
}
