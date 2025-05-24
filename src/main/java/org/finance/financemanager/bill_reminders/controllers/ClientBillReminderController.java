package org.finance.financemanager.bill_reminders.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.entities.BillReminderOrderBy;
import org.finance.financemanager.bill_reminders.payloads.BillReminderPayResponse;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.finance.financemanager.bill_reminders.services.BillReminderService;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Operation(
            description = "Fetch paginated results of all bill reminders made by the user."
    )
    @GetMapping("/")
    public ListResponseDto<BillReminderResponseDto> getUsersBillReminders(
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

    @Operation(
            description = "Get details for a specific bill reminder made by a user by providing a bill reminder ID."
    )
    @GetMapping("/specific")
    public BillReminderResponseDto getBillReminderById(@RequestParam String billReminderId) {
        return service.getBillReminderById(billReminderId);
    }

    @Operation(
            description = "This endpoint is used for creating and storing a new bill reminder."
    )
    @PostMapping("/")
    public BillReminderResponseDto createBillReminder(@RequestBody BillReminderRequestDto billReminderRequest) {
        return service.createBillReminder(billReminderRequest);
    }

    @Operation(
            description = "Update an existing bill reminder by providing bill reminder ID alongside the new form data."
    )
    @PatchMapping("/")
    public BillReminderResponseDto updateBillReminder(@RequestParam String billReminderId , @RequestBody BillReminderRequestDto billReminderRequest) {
        return service.updateBillReminder(billReminderId, billReminderRequest);
    }

    @Operation(
            description = "Delete a specific bill reminder made by a user by providing a bill reminder ID."
    )
    @DeleteMapping("/")
    public SuccessResponseDto deleteBillReminder(@RequestParam String billReminderId) {
        return service.deleteBillReminder(billReminderId);
    }

    @Operation(
            description = "Toggle paid status for a specific bill reminder by providing a bill reminder ID."
    )
    @PatchMapping("/paid-status/")
    public BillReminderPayResponse editBillReminderPayment(@RequestParam String billReminderId) {
        return service.editBillReminderPayment(billReminderId);
    }
}
