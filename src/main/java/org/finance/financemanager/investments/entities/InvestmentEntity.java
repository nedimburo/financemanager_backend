package org.finance.financemanager.investments.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.investments.Investment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "investments")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class InvestmentEntity extends Auditable implements Investment {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

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

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
}
