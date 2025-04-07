package org.finance.financemanager.bill_reminders.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.mappers.BillReminderMapper;
import org.finance.financemanager.bill_reminders.payloads.BillReminderDetailsResponseDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderPayResponse;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.bill_reminders.specifications.BillReminderSpecification;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BillReminderService {

    private final BillReminderRepository repository;
    private final BillReminderMapper billReminderMapper;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    public Page<BillReminderResponseDto> getUsersBillReminders(Pageable pageable, String query) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        try {
            Specification<BillReminderEntity> spec = BillReminderSpecification.filterBillReminders(query, userId);
            return repository.findAll(spec, pageable).map(billReminderMapper::toDto);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users bill reminders: ", e);
        }
    }

    @Transactional
    public BillReminderResponseDto getBillReminderById(String billReminderId) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        BillReminderEntity billReminder = getBillReminder(billReminderId);

        try {
            return billReminderMapper.toDto(billReminder);
        } catch (Exception e){
            throw new RuntimeException("Error getting bill reminder by id: " + billReminderId, e);
        }
    }

    @Transactional
    public BillReminderResponseDto createBillReminder(BillReminderRequestDto billReminderRequest) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        UserEntity user = userService.getUser(userId);

        try {
            BillReminderEntity newBillReminder = billReminderMapper.toEntity(billReminderRequest);
            newBillReminder.setId(UUID.randomUUID().toString());
            newBillReminder.setIsPaid(false);
            newBillReminder.setUser(user);
            BillReminderEntity savedBillReminder = repository.save(newBillReminder);

            transactionService.createTransactionFromBillReminder(savedBillReminder);

            BillReminderResponseDto response = billReminderMapper.toDto(savedBillReminder);
            response.setMessage("Bill reminder has been successfully created");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating bill reminder: ", e);
        }
    }

    @Transactional
    public BillReminderResponseDto updateBillReminder(String billReminderId, BillReminderRequestDto billReminderRequest) {
        try {
            BillReminderEntity updatedBillReminder = getBillReminder(billReminderId);
            if (billReminderRequest.getBillName() != null) { updatedBillReminder.setBillName(billReminderRequest.getBillName()); }
            if (billReminderRequest.getAmount() != null) { updatedBillReminder.setAmount(billReminderRequest.getAmount()); }
            if (billReminderRequest.getReceivedDate() != null) { updatedBillReminder.setReceivedDate(billReminderRequest.getReceivedDate()); }
            if (billReminderRequest.getDueDate() != null) { updatedBillReminder.setDueDate(billReminderRequest.getDueDate()); }
            repository.save(updatedBillReminder);

            BillReminderResponseDto response = billReminderMapper.toDto(updatedBillReminder);
            response.setMessage("Bill reminder has been successfully updated");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error updating bill reminder: ", e);
        }
    }

    @Transactional
    public ResponseEntity<SuccessResponseDto> deleteBillReminder(String billReminderId) {
        try {
            BillReminderEntity billReminder = getBillReminder(billReminderId);
            repository.delete(billReminder);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(SuccessResponseDto.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Bill reminder has been successfully deleted.")
                        .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                        .build());
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

    @Transactional
    public ResponseEntity<BillReminderPayResponse> editBillReminderPayment(String billReminderId) {
        try {
            BillReminderEntity updatedBill = getBillReminder(billReminderId);
            updatedBill.setIsPaid(!updatedBill.getIsPaid());
            BillReminderPayResponse response = new BillReminderPayResponse();
            response.setPaymentStatus(updatedBill.getIsPaid());
            response.setMessage("Bill reminder has been successfully updated");
            response.setUpdatedDate(LocalDateTime.now().toString());
            repository.save(updatedBill);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating bill reminder payment details: ", e);
        }
    }

    public BillReminderEntity getBillReminder(String billReminderId) {
        return repository.findById(billReminderId)
                .orElseThrow(() -> new EntityNotFoundException("Bill reminder not found with id: " + billReminderId));
    }
}
