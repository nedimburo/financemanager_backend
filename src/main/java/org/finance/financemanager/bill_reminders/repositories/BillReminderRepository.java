package org.finance.financemanager.bill_reminders.repositories;

import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BillReminderRepository extends JpaRepository<BillReminderEntity, UUID>, JpaSpecificationExecutor<BillReminderEntity> {
    @Query("SELECT SUM(b.amount) FROM BillReminderEntity b WHERE b.isPaid = true AND b.user.id = :userId")
    BigDecimal findTotalPaidBillsByUserId(@Param("userId") String userId);
    @Query("SELECT SUM(b.amount) FROM BillReminderEntity b WHERE b.isPaid = false AND b.user.id = :userId")
    BigDecimal findTotalUnpaidBillsByUserId(@Param("userId") String userId);
    Optional<BillReminderEntity> findFirstByUserIdAndIsPaidFalseOrderByDueDateAsc(String userId);

}
