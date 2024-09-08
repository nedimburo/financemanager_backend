package org.finance.financemanager.bill_reminders.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.bill_reminders.BillReminder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bill_reminders")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class BillReminderEntity implements BillReminder {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

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

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;
}
