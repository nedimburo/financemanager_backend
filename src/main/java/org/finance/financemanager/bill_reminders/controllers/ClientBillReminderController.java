package org.finance.financemanager.bill_reminders.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.services.BillReminderService;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getUsersBillReminders(pageable);
    }

    @GetMapping("/{billReminderId}")
    public ResponseEntity<BillReminderResponseDto> getBillReminderById(@PathVariable String billReminderId) {
        return service.getBillReminderById(billReminderId);
    }

    @PostMapping("/create")
    public ResponseEntity<BillReminderResponseDto> createBillReminder(@RequestBody BillReminderRequestDto billReminderRequest) {
        return service.createBillReminder(billReminderRequest);
    }

    @PatchMapping("/{billReminderId}")
    public ResponseEntity<BillReminderResponseDto> updateBillReminder(@PathVariable String billReminderId , @RequestBody BillReminderRequestDto billReminderRequest) {
        return service.updateBillReminder(billReminderId, billReminderRequest);
    }

    @PatchMapping("/pay/{billReminderId}")
    public ResponseEntity<BillReminderResponseDto> payBillReminder(@PathVariable String billReminderId) {
        return service.payBillReminder(billReminderId);
    }

    @DeleteMapping("/{billReminderId}")
    public ResponseEntity<DeleteResponseDto> deleteBillReminder(@PathVariable String billReminderId) {
        return service.deleteBillReminder(billReminderId);
    }
}
