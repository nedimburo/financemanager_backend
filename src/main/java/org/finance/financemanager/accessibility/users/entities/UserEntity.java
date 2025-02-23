package org.finance.financemanager.accessibility.users.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.users.User;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class UserEntity extends Auditable implements User {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @CreatedDate
    @Column(name = "created", updatable = false, nullable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", updatable = false, nullable = false)
    private LocalDateTime updated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BudgetEntity> budgets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillReminderEntity> billReminders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SavingEntity> savings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvestmentEntity> investments;
}
