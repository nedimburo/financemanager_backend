package org.finance.financemanager.currency_rates.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.currency_rates.CurrencyRate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "currency_rates")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class CurrencyRateEntity implements CurrencyRate {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private double rate;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;
}
