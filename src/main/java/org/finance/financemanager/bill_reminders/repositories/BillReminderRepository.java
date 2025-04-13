package org.finance.financemanager.bill_reminders.repositories;

import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface BillReminderRepository extends JpaRepository<BillReminderEntity, String>, JpaSpecificationExecutor<BillReminderEntity> {
    @Query("SELECT SUM(b.amount) FROM BillReminderEntity b WHERE b.isPaid = true AND b.user.id = :userId")
    BigDecimal findTotalPaidBillsByUserId(@Param("userId") String userId);
    @Query("SELECT SUM(b.amount) FROM BillReminderEntity b WHERE b.isPaid = false AND b.user.id = :userId")
    BigDecimal findTotalUnpaidBillsByUserId(@Param("userId") String userId);
    @Query("SELECT b FROM BillReminderEntity b WHERE b.isPaid = false AND b.user.id = :userId ORDER BY b.dueDate ASC")
    BillReminderEntity findClosestUnpaidBillByUserId(@Param("userId") String userId);
}
