package org.finance.financemanager.savings.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.payloads.SavingAmountResponseDto;
import org.finance.financemanager.savings.payloads.SavingDetailsResponseDto;
import org.finance.financemanager.savings.payloads.SavingRequestDto;
import org.finance.financemanager.savings.payloads.SavingResponseDto;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository repository;
    private final UserService userService;

    @Transactional
    public Page<SavingResponseDto> getUsersSavings(Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserId(uid, pageable)
                    .map(saving -> new SavingResponseDto(
                            saving.getId(),
                            saving.getUser().getId(),
                            saving.getGoalName(),
                            saving.getTargetAmount(),
                            saving.getCurrentAmount(),
                            saving.getStartDate(),
                            saving.getTargetDate(),
                            null,
                            saving.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users savings: ", e);
        }
    }

    @Transactional
    public Page<SavingResponseDto> searchUsersSavings(String goalName, Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserIdAndGoalNameContainingIgnoreCase(uid, goalName, pageable)
                    .map(saving -> new SavingResponseDto(
                            saving.getId(),
                            saving.getUser().getId(),
                            saving.getGoalName(),
                            saving.getTargetAmount(),
                            saving.getCurrentAmount(),
                            saving.getStartDate(),
                            saving.getTargetDate(),
                            null,
                            saving.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users savings: ", e);
        }
    }

    @Transactional
    public ResponseEntity<SavingResponseDto> getSavingById(String savingId) {
        try {
            SavingEntity saving = getSaving(savingId);
            SavingResponseDto response = formatSavingResponse(saving);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error getting saving by id: " + savingId, e);
        }
    }

    @Transactional
    public ResponseEntity<SavingResponseDto> createSaving(SavingRequestDto savingRequest) {
        try {
            String uid = Auth.getUserId();
            UserEntity user = userService.getUser(uid);

            SavingEntity newSaving = new SavingEntity();
            newSaving.setId(UUID.randomUUID().toString());
            newSaving.setGoalName(savingRequest.getGoalName());
            newSaving.setTargetAmount(savingRequest.getTargetAmount());
            newSaving.setCurrentAmount(savingRequest.getCurrentAmount());
            newSaving.setStartDate(savingRequest.getStartDate());
            newSaving.setTargetDate(savingRequest.getTargetDate());
            newSaving.setUser(user);
            SavingEntity savedSaving = repository.save(newSaving);

            SavingResponseDto response = formatSavingResponse(savedSaving);
            response.setMessage("Saving has been successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating saving: ", e);
        }
    }

    @Transactional
    public ResponseEntity<SavingResponseDto> updateSaving(String savingId, SavingRequestDto savingRequest) {
        try {
            SavingEntity updatedSaving = getSaving(savingId);
            if (savingRequest.getGoalName() != null) { updatedSaving.setGoalName(savingRequest.getGoalName()); }
            if (savingRequest.getTargetAmount() != null) { updatedSaving.setTargetAmount(savingRequest.getTargetAmount()); }
            if (savingRequest.getCurrentAmount() != null) { updatedSaving.setCurrentAmount(savingRequest.getCurrentAmount()); }
            if (savingRequest.getStartDate() != null) { updatedSaving.setStartDate(savingRequest.getStartDate()); }
            if (savingRequest.getTargetDate() != null) { updatedSaving.setTargetDate(savingRequest.getTargetDate()); }
            repository.save(updatedSaving);

            SavingResponseDto response = formatSavingResponse(updatedSaving);
            response.setMessage("Saving has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating saving: ", e);
        }
    }

    @Transactional
    public ResponseEntity<DeleteResponseDto> deleteSaving(String savingId) {
        try {
            SavingEntity saving = getSaving(savingId);
            repository.delete(saving);

            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(savingId);
            response.setMessage("Saving has been successfully deleted");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error deleting saving: " + savingId, e);
        }
    }

    @Transactional
    public ResponseEntity<SavingDetailsResponseDto> getSavingDetails() {
        try {
            String uid = Auth.getUserId();
            SavingEntity closestSaving = repository.findSavingWithSmallestDifferenceByUserId(uid);
            SavingEntity furthestSaving = repository.findSavingWithBiggestDifferenceByUserId(uid);
            SavingDetailsResponseDto response = new SavingDetailsResponseDto();
            response.setClosestSavingGoalName(closestSaving != null ? closestSaving.getGoalName() : "N/A");
            response.setClosestSavingCurrentAmount(closestSaving != null ? closestSaving.getCurrentAmount() : BigDecimal.ZERO);
            response.setClosestSavingTargetAmount(closestSaving != null ? closestSaving.getTargetAmount() : BigDecimal.ZERO);
            response.setFurthestSavingGoalName(furthestSaving != null ? furthestSaving.getGoalName() : "N/A");
            response.setFurthestSavingCurrentAmount(furthestSaving != null ? furthestSaving.getCurrentAmount() : BigDecimal.ZERO);
            response.setFurthestSavingTargetAmount(furthestSaving != null ? furthestSaving.getTargetAmount() : BigDecimal.ZERO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting savings details: ", e);
        }
    }

    @Transactional
    public ResponseEntity<SavingAmountResponseDto> editSavedAmount(String savingId, BigDecimal savedAmount) {
        try {
            SavingEntity updatedSaving = getSaving(savingId);
            if (savedAmount != null) { updatedSaving.setCurrentAmount(savedAmount); }
            SavingAmountResponseDto response = new SavingAmountResponseDto();
            response.setCurrentAmount(updatedSaving.getCurrentAmount());
            response.setMessage("Saving has been successfully updated");
            response.setUpdatedDate(LocalDateTime.now().toString());
            repository.save(updatedSaving);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error editing saved amount for saving: ", e);
        }
    }

    private SavingResponseDto formatSavingResponse(SavingEntity saving) {
        SavingResponseDto response = new SavingResponseDto();
        response.setSavingId(saving.getId());
        response.setUserId(saving.getUser().getId());
        response.setGoalName(saving.getGoalName());
        response.setTargetAmount(saving.getTargetAmount() != null ? saving.getTargetAmount() : BigDecimal.ZERO);
        response.setCurrentAmount(saving.getCurrentAmount() != null ? saving.getCurrentAmount() : BigDecimal.ZERO);
        response.setStartDate(saving.getStartDate());
        response.setTargetDate(saving.getTargetDate());
        response.setCreatedDate(saving.getCreated().toString());
        return response;
    }

    public SavingEntity getSaving(String savingId) {
        return repository.findById(savingId)
                .orElseThrow(() -> new EntityNotFoundException("Saving not found with id: " + savingId));
    }
}
