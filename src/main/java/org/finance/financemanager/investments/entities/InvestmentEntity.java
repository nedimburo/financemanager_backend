package org.finance.financemanager.investments.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.investments.Investment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "investments")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class InvestmentEntity extends Auditable {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    @Column(name = "investment_name")
    private String investmentName;

    @Column(name = "amount_invested")
    private BigDecimal amountInvested;

    @Column(name = "current_value")
    private BigDecimal currentValue;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @ManyToMany
    @JoinTable(
            name = "files_investments",
            joinColumns = @JoinColumn(name = "investment_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileEntity> files;
}
