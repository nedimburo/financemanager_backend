package org.finance.financemanager.bill_reminders.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BillReminderService {

    private final BillReminderRepository repository;

    private BillReminderEntity getBillReminder(String billReminderId) {
        return repository.findById(billReminderId)
                .orElseThrow(() -> new EntityNotFoundException("Bill reminder not found with id: " + billReminderId));
    }
}
