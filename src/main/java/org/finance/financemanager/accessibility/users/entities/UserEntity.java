package org.finance.financemanager.accessibility.users.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.transactions.entities.TransactionEntity;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class UserEntity extends Auditable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
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
