package org.finance.financemanager.investments.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository repository;

    private InvestmentEntity getInvestment(String investmentId) {
        return repository.findById(investmentId)
                .orElseThrow(() -> new EntityNotFoundException("Investment not found with id: " + investmentId));
    }
}
