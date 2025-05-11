package org.finance.financemanager.currency_rates.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.common.entities.Auditable;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "currency_rates")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class CurrencyRateEntity extends Auditable {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private double rate;
}
