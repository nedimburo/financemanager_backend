package org.finance.financemanager.savings.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository repository;

    private SavingEntity getSaving(String savingId) {
        return repository.findById(savingId)
                .orElseThrow(() -> new EntityNotFoundException("Saving not found with id: " + savingId));
    }
}
