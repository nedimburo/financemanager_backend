package org.finance.financemanager.bill_reminders.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.entities.BillReminderOrderBy;
import org.finance.financemanager.bill_reminders.payloads.BillReminderDetailsResponseDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderPayResponse;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.services.BillReminderService;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("client/bill-reminders")
@Tags(value = {@Tag(name = "Client | Bill Reminders"), @Tag(name = OPERATION_ID_NAME + "ClientBillReminders")})
public class ClientBillReminderController {

    private final BillReminderService service;

    @GetMapping("/")
    public Page<BillReminderResponseDto> getUsersBillReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BillReminderOrderBy orderBy,
            @RequestParam(required = false) Boolean orderDirection
    ){
        Sort.Direction direction = (orderDirection != null && orderDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, (orderBy != null) ? orderBy.getColumn() : "id"));

        return service.getUsersBillReminders(pageable, query);
    }

    @GetMapping("/specific")
    public BillReminderResponseDto getBillReminderById(@RequestParam String billReminderId) {
        return service.getBillReminderById(billReminderId);
    }

    @PostMapping("/")
    public BillReminderResponseDto createBillReminder(@RequestBody BillReminderRequestDto billReminderRequest) {
        return service.createBillReminder(billReminderRequest);
    }

    @PatchMapping("/")
    public BillReminderResponseDto updateBillReminder(@RequestParam String billReminderId , @RequestBody BillReminderRequestDto billReminderRequest) {
        return service.updateBillReminder(billReminderId, billReminderRequest);
    }

    @DeleteMapping("/")
    public ResponseEntity<SuccessResponseDto> deleteBillReminder(@RequestParam String billReminderId) {
        return service.deleteBillReminder(billReminderId);
    }

    @GetMapping("/details")
    public ResponseEntity<BillReminderDetailsResponseDto> getBillRemindersDetails() {
        return service.getBillRemindersDetails();
    }

    @PatchMapping("/paid-status/")
    public ResponseEntity<BillReminderPayResponse> editBillReminderPayment(@RequestParam String billReminderId) {
        return service.editBillReminderPayment(billReminderId);
    }
}
