package org.finance.financemanager.investments.repositories;

import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface InvestmentRepository extends JpaRepository<InvestmentEntity, UUID>, JpaSpecificationExecutor<InvestmentEntity> {
    @Query("SELECT SUM(i.amountInvested) FROM InvestmentEntity i WHERE i.user.id = :userId")
    BigDecimal findInvestmentAmountInvestedTotalByUserId(@Param("userId") String userId);
    @Query("SELECT SUM(i.currentValue) FROM InvestmentEntity i WHERE i.user.id = :userId")
    BigDecimal findInvestmentCurrentValueTotalByUserId(@Param("userId") String userId);
    @Query("SELECT i FROM InvestmentEntity i WHERE i.user.id = :userId AND i.amountInvested = " +
            "(SELECT MAX(i2.amountInvested) FROM InvestmentEntity i2 WHERE i2.user.id = :userId)")
    InvestmentEntity findHighestInvestmentAmountInvestedByUserId(@Param("userId") String userId);
    @Query("SELECT i FROM InvestmentEntity i WHERE i.user.id = :userId AND i.currentValue = " +
            "(SELECT MAX(i2.currentValue) FROM InvestmentEntity i2 WHERE i2.user.id = :userId)")
    InvestmentEntity findHighestInvestmentCurrentValueByUserId(@Param("userId") String userId);
}
