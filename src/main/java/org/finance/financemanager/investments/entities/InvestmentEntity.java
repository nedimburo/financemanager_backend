package org.finance.financemanager.investments.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.investments.Investment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "investments")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class InvestmentEntity implements Investment {

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

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;
}
