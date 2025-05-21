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
import org.finance.financemanager.bill_reminders.payloads.BillReminderPayResponse;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.bill_reminders.specifications.BillReminderSpecification;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.PaginationResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ListResponseDto<BillReminderResponseDto> getUsersBillReminders(Pageable pageable, String query) {
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
            Page<BillReminderResponseDto> billRemindersPage = repository.findAll(spec, pageable).map(billReminderMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) billRemindersPage.getTotalElements(),
                    billRemindersPage.getNumber(),
                    billRemindersPage.getTotalPages()
            );

            return new ListResponseDto<>(billRemindersPage.getContent(), paging);
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

        UUID billReminderUuid;
        try {
            billReminderUuid = UUID.fromString(billReminderId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting bill reminder id to UUID.");
        }


        BillReminderEntity billReminder;
        try {
            billReminder = getBillReminder(billReminderUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Bill reminder with id: " + billReminderId + " doesn't exist");
        }

        try {
            return billReminderMapper.toDto(billReminder);
        } catch (Exception e){
            throw new RuntimeException("Error getting bill reminder by id: " + billReminderId + " " + e.getMessage());
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
        UUID billReminderUuid;
        try {
            billReminderUuid = UUID.fromString(billReminderId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting bill reminder id to UUID.");
        }


        BillReminderEntity updatedBillReminder;
        try {
            updatedBillReminder = getBillReminder(billReminderUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Bill reminder with id: " + billReminderId + " doesn't exist");
        }

        try {
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
        UUID billReminderUuid;
        try {
            billReminderUuid = UUID.fromString(billReminderId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting bill reminder id to UUID.");
        }


        BillReminderEntity billReminder;
        try {
            billReminder = getBillReminder(billReminderUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Bill reminder with id: " + billReminderId + " doesn't exist");
        }

        try {
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
    public ResponseEntity<BillReminderPayResponse> editBillReminderPayment(String billReminderId) {
        UUID billReminderUuid;
        try {
            billReminderUuid = UUID.fromString(billReminderId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting bill reminder id to UUID.");
        }


        BillReminderEntity updatedBill;
        try {
            updatedBill = getBillReminder(billReminderUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Bill reminder with id: " + billReminderId + " doesn't exist");
        }

        try {
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

    public BillReminderEntity getBillReminder(UUID billReminderId) {
        return repository.findById(billReminderId)
                .orElseThrow(() -> new EntityNotFoundException("Bill reminder not found with id: " + billReminderId));
    }
}
