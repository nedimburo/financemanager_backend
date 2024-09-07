package org.finance.financemanager.bill_reminders.repositories;

import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillReminderRepository extends JpaRepository<BillReminderEntity, String> {
}
