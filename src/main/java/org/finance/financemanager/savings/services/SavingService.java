package org.finance.financemanager.savings.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.PaginationResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.mappers.SavingMapper;
import org.finance.financemanager.savings.payloads.SavingAmountResponseDto;
import org.finance.financemanager.savings.payloads.SavingRequestDto;
import org.finance.financemanager.savings.payloads.SavingResponseDto;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.finance.financemanager.savings.specifications.SavingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository repository;
    private final SavingMapper savingMapper;
    private final UserService userService;

    @Transactional
    public ListResponseDto<SavingResponseDto> getUsersSavings(Pageable pageable, String query) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        try {
            Specification<SavingEntity> spec = SavingSpecification.filterTransactions(query, userId);
            Page<SavingResponseDto> savingsPage = repository.findAll(spec, pageable).map(savingMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) savingsPage.getTotalElements(),
                    savingsPage.getNumber(),
                    savingsPage.getTotalPages()
            );

            return new ListResponseDto<>(savingsPage.getContent(), paging);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users savings: ", e);
        }
    }

    @Transactional
    public SavingResponseDto getSavingById(String savingId) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        Boolean userExists = userService.doesUserExist(userId);
        if (!userExists) {
            throw new ResourceNotFoundException("User with ID: " + userId + " doesn't exist");
        }

        UUID savingUuid;
        try {
            savingUuid = UUID.fromString(savingId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting saving id to UUID.");
        }


        SavingEntity saving;
        try {
            saving = getSaving(savingUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Saving with id: " + savingId + " doesn't exist");
        }

        try {
            return savingMapper.toDto(saving);
        } catch (Exception e){
            throw new RuntimeException("Error getting saving by id: " + savingId, e);
        }
    }

    @Transactional
    public SavingResponseDto createSaving(SavingRequestDto savingRequest) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        UserEntity user = userService.getUser(userId);

        try {
            SavingEntity newSaving = savingMapper.toEntity(savingRequest);
            newSaving.setUser(user);
            SavingEntity savedSaving = repository.save(newSaving);

            SavingResponseDto response = savingMapper.toDto(savedSaving);
            response.setMessage("Saving has been successfully created");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating saving: ", e);
        }
    }

    @Transactional
    public SavingResponseDto updateSaving(String savingId, SavingRequestDto savingRequest) {
        UUID savingUuid;
        try {
            savingUuid = UUID.fromString(savingId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting saving id to UUID.");
        }


        SavingEntity updatedSaving;
        try {
            updatedSaving = getSaving(savingUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Saving with id: " + savingId + " doesn't exist");
        }

        try {
            if (savingRequest.getGoalName() != null) { updatedSaving.setGoalName(savingRequest.getGoalName()); }
            if (savingRequest.getTargetAmount() != null) { updatedSaving.setTargetAmount(savingRequest.getTargetAmount()); }
            if (savingRequest.getCurrentAmount() != null) { updatedSaving.setCurrentAmount(savingRequest.getCurrentAmount()); }
            if (savingRequest.getStartDate() != null) { updatedSaving.setStartDate(savingRequest.getStartDate()); }
            if (savingRequest.getTargetDate() != null) { updatedSaving.setTargetDate(savingRequest.getTargetDate()); }
            repository.save(updatedSaving);

            SavingResponseDto response = savingMapper.toDto(updatedSaving);
            response.setMessage("Saving has been successfully updated");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error updating saving: ", e);
        }
    }

    @Transactional
    public SuccessResponseDto deleteSaving(String savingId) {
        UUID savingUuid;
        try {
            savingUuid = UUID.fromString(savingId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting saving id to UUID.");
        }


        SavingEntity saving;
        try {
            saving = getSaving(savingUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Saving with id: " + savingId + " doesn't exist");
        }

        try {
            repository.delete(saving);

            return SuccessResponseDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.CREATED.value())
                            .message("Saving has been deleted successfully.")
                            .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                            .build();
        } catch (Exception e){
            throw new RuntimeException("Error deleting saving: " + savingId, e);
        }
    }

    @Transactional
    public SavingAmountResponseDto editSavedAmount(String savingId, BigDecimal savedAmount) {
        UUID savingUuid;
        try {
            savingUuid = UUID.fromString(savingId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting saving id to UUID.");
        }


        SavingEntity updatedSaving;
        try {
            updatedSaving = getSaving(savingUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Saving with id: " + savingId + " doesn't exist");
        }

        try {
            if (savedAmount != null) { updatedSaving.setCurrentAmount(savedAmount); }
            SavingAmountResponseDto response = new SavingAmountResponseDto();
            response.setCurrentAmount(updatedSaving.getCurrentAmount());
            response.setMessage("Saving has been successfully updated");
            response.setUpdatedDate(LocalDateTime.now().toString());
            repository.save(updatedSaving);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error editing saved amount for saving: ", e);
        }
    }

    public SavingEntity getSaving(UUID savingId) {
        return repository.findById(savingId)
                .orElseThrow(() -> new EntityNotFoundException("Saving not found with id: " + savingId));
    }
}
