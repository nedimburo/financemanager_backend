package org.finance.financemanager.savings.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.savings.Saving;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "savings")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class SavingEntity implements Saving {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "goal_name")
    private String goalName;

    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @Column(name = "current_amount")
    private BigDecimal currentAmount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;
}
