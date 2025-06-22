package org.finance.financemanager.budgets.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.files.entities.FileEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "budgets")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class BudgetEntity extends Auditable {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

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

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();
}
