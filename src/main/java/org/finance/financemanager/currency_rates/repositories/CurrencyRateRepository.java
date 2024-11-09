package org.finance.financemanager.currency_rates.repositories;

import org.finance.financemanager.currency_rates.entities.CurrencyRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {
}
