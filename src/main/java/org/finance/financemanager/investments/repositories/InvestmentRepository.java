package org.finance.financemanager.investments.repositories;

import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepository extends JpaRepository<InvestmentEntity, String> {
    Page<InvestmentEntity> findAllByUserId(String userId, Pageable pageable);
    Page<InvestmentEntity> findAllByUserIdAndInvestmentNameContainingIgnoreCase(String userId, String investmentName, Pageable pageable);
}
