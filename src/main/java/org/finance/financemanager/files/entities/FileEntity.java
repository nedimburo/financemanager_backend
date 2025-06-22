package org.finance.financemanager.files.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.transactions.entities.TransactionEntity;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "files")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class FileEntity extends Auditable {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "url", length = 500, nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    private TransactionEntity transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    private BillReminderEntity billReminder;

    @ManyToOne(fetch = FetchType.LAZY)
    private InvestmentEntity investment;

    @ManyToOne(fetch = FetchType.LAZY)
    private SavingEntity saving;

    @ManyToOne(fetch = FetchType.LAZY)
    private BudgetEntity budget;
}
