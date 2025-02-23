package org.finance.financemanager.currency_rates.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.common.entities.Auditable;
import org.finance.financemanager.currency_rates.CurrencyRate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "currency_rates")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class CurrencyRateEntity extends Auditable implements CurrencyRate {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private double rate;

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
}
