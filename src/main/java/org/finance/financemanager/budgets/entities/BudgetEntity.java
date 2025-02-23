package org.finance.financemanager.budgets.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.budgets.Budget;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "budgets")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class BudgetEntity extends Auditable implements Budget {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "budget_name")
    private String budgetName;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private FinanceCategory category;

    @Column(name = "budget_limit")
    private BigDecimal budgetLimit;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
}
