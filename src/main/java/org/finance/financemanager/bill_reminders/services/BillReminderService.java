package org.finance.financemanager.bill_reminders.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.payloads.BillReminderDetailsResponseDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BillReminderService {

    private final BillReminderRepository repository;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    public Page<BillReminderResponseDto> getUsersBillReminders(Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserId(uid, pageable)
                    .map(billReminder -> new BillReminderResponseDto(
                            billReminder.getId(),
                            billReminder.getUser().getId(),
                            billReminder.getBillName(),
                            billReminder.getAmount(),
                            billReminder.getReceivedDate().toString(),
                            billReminder.getDueDate().toString(),
                            billReminder.getIsPaid(),
                            null,
                            billReminder.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users bill reminders: ", e);
        }
    }

    @Transactional
    public Page<BillReminderResponseDto> searchUsersBillReminders(String billName, Pageable pageable){
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserIdAndBillNameContainingIgnoreCase(uid, billName, pageable)
                    .map(billReminder -> new BillReminderResponseDto(
                            billReminder.getId(),
                            billReminder.getUser().getId(),
                            billReminder.getBillName(),
                            billReminder.getAmount(),
                            billReminder.getReceivedDate().toString(),
                            billReminder.getDueDate().toString(),
                            billReminder.getIsPaid(),
                            null,
                            billReminder.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users bill reminders: ", e);
        }
    }

    @Transactional
    public ResponseEntity<BillReminderResponseDto> getBillReminderById(String billReminderId) {
        try {
            BillReminderEntity billReminder = getBillReminder(billReminderId);
            BillReminderResponseDto response = formatBillReminderResponse(billReminder);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error getting bill reminder by id: " + billReminderId, e);
        }
    }

    @Transactional
    public ResponseEntity<BillReminderResponseDto> createBillReminder(BillReminderRequestDto billReminderRequest) {
        try {
            String uid = Auth.getUserId();
            UserEntity user = userService.getUser(uid);

            BillReminderEntity newBillReminder = new BillReminderEntity();
            newBillReminder.setId(UUID.randomUUID().toString());
            newBillReminder.setBillName(billReminderRequest.getBillName());
            newBillReminder.setAmount(billReminderRequest.getAmount());
            newBillReminder.setReceivedDate(billReminderRequest.getReceivedDate());
            newBillReminder.setDueDate(billReminderRequest.getDueDate());
            newBillReminder.setIsPaid(false);
            newBillReminder.setCreated(LocalDateTime.now());
            newBillReminder.setUpdated(LocalDateTime.now());
            newBillReminder.setUser(user);
            repository.save(newBillReminder);

            transactionService.createTransactionFromBillReminder(newBillReminder);

            BillReminderResponseDto response = formatBillReminderResponse(newBillReminder);
            response.setMessage("Bill reminder has been successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating bill reminder: ", e);
        }
    }

    @Transactional
    public ResponseEntity<BillReminderResponseDto> updateBillReminder(String billReminderId, BillReminderRequestDto billReminderRequest) {
        try {
            BillReminderEntity updatedBillReminder = getBillReminder(billReminderId);
            if (billReminderRequest.getBillName() != null) { updatedBillReminder.setBillName(billReminderRequest.getBillName()); }
            if (billReminderRequest.getAmount() != null) { updatedBillReminder.setAmount(billReminderRequest.getAmount()); }
            if (billReminderRequest.getReceivedDate() != null) { updatedBillReminder.setReceivedDate(billReminderRequest.getReceivedDate()); }
            if (billReminderRequest.getDueDate() != null) { updatedBillReminder.setDueDate(billReminderRequest.getDueDate()); }
            updatedBillReminder.setUpdated(LocalDateTime.now());
            repository.save(updatedBillReminder);

            BillReminderResponseDto response = formatBillReminderResponse(updatedBillReminder);
            response.setMessage("Bill reminder has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating bill reminder: ", e);
        }
    }

    @Transactional
    public ResponseEntity<BillReminderResponseDto> payBillReminder(String billReminderId) {
        try {
            BillReminderEntity updatedBillReminder = getBillReminder(billReminderId);
            updatedBillReminder.setIsPaid(true);
            updatedBillReminder.setUpdated(LocalDateTime.now());
            repository.save(updatedBillReminder);

            BillReminderResponseDto response = formatBillReminderResponse(updatedBillReminder);
            response.setMessage("Bill reminder has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating bill reminder: ", e);
        }
    }

    @Transactional
    public ResponseEntity<DeleteResponseDto> deleteBillReminder(String billReminderId) {
        try {
            BillReminderEntity billReminder = getBillReminder(billReminderId);
            repository.delete(billReminder);

            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(billReminderId);
            response.setMessage("Bill reminder has been successfully deleted");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error deleting bill reminder: " + billReminderId, e);
        }
    }

    @Transactional
    public ResponseEntity<BillReminderDetailsResponseDto> getBillRemindersDetails() {
        try {
            String uid = Auth.getUserId();
            BigDecimal totalPaidAmount = repository.findTotalPaidBillsByUserId(uid);
            BigDecimal totalUnpaidAmount = repository.findTotalUnpaidBillsByUserId(uid);
            BillReminderEntity billReminderToPay = repository.findClosestUnpaidBillByUserId(uid);
            BillReminderDetailsResponseDto response = new BillReminderDetailsResponseDto();
            response.setTotalAmountPaidBills(totalPaidAmount != null ? totalPaidAmount : BigDecimal.ZERO);
            response.setTotalAmountUnpaidBills(totalUnpaidAmount != null ? totalUnpaidAmount : BigDecimal.ZERO);
            response.setBillToPayName(billReminderToPay != null ? billReminderToPay.getBillName() : "N/A");
            response.setBillToPayAmount(billReminderToPay != null ? billReminderToPay.getAmount() : BigDecimal.ZERO);
            response.setBillToPayDueDate(billReminderToPay != null ? billReminderToPay.getDueDate().toString() : "N/A");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error getting bill reminder details: ", e);
        }
    }

    private BillReminderResponseDto formatBillReminderResponse(BillReminderEntity billReminder) {
        BillReminderResponseDto response = new BillReminderResponseDto();
        response.setBillReminderId(billReminder.getId());
        response.setUserId(billReminder.getUser().getId());
        response.setBillName(billReminder.getBillName());
        response.setAmount(billReminder.getAmount() != null ? billReminder.getAmount() : BigDecimal.ZERO);
        response.setReceivedDate(billReminder.getReceivedDate().toString());
        response.setDueDate(billReminder.getDueDate().toString());
        response.setIsPaid(billReminder.getIsPaid());
        response.setCreatedDate(billReminder.getCreated().toString());
        return response;
    }

    public BillReminderEntity getBillReminder(String billReminderId) {
        return repository.findById(billReminderId)
                .orElseThrow(() -> new EntityNotFoundException("Bill reminder not found with id: " + billReminderId));
    }
}
