package org.finance.financemanager.bill_reminders.mappers;

import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.payloads.BillReminderRequestDto;
import org.finance.financemanager.bill_reminders.payloads.BillReminderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BillReminderMapper {
    BillReminderMapper INSTANCE = Mappers.getMapper(BillReminderMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "billReminderId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    BillReminderResponseDto toDto(BillReminderEntity billReminder);

    BillReminderEntity toEntity(BillReminderRequestDto request);
}
