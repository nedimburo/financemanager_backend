package org.finance.financemanager.bill_reminders.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.files.entities.FileEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bill_reminders")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class BillReminderEntity extends Auditable {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "bill_name")
    private String billName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @ManyToMany
    @JoinTable(
            name = "files_bill_reminders",
            joinColumns = @JoinColumn(name = "bill_reminder_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileEntity> files;
}
