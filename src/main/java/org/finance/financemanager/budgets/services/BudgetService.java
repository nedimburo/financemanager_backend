package org.finance.financemanager.budgets.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.mappers.BudgetMapper;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.payloads.BudgetSpecificResponseDto;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.budgets.specifications.BudgetSpecification;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.ListResponseDto;
import org.finance.financemanager.common.payloads.PaginationResponseDto;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository repository;
    private final BudgetMapper budgetMapper;
    private final UserService userService;

    @Transactional
    public ListResponseDto<BudgetResponseDto> getUsersBudgets(Pageable pageable, String query, FinanceCategory category) {
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
            Specification<BudgetEntity> spec = BudgetSpecification.filterBudgets(query, category, userId);
            Page<BudgetResponseDto> budgetsPage = repository.findAll(spec, pageable).map(budgetMapper::toDto);

            PaginationResponseDto paging = new PaginationResponseDto(
                    (int) budgetsPage.getTotalElements(),
                    budgetsPage.getNumber(),
                    budgetsPage.getTotalPages()
            );

            return new ListResponseDto<>(budgetsPage.getContent(), paging);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users budgets: ", e);
        }
    }

    @Transactional
    public BudgetSpecificResponseDto getBudgetById(String budgetId) {
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

        UUID budgetUuid;
        try {
            budgetUuid = UUID.fromString(budgetId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting budget id to UUID.");
        }


        BudgetEntity budget;
        try {
            budget = getBudget(budgetUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Budget with id: " + budgetId + " doesn't exist");
        }

        try {
            return budgetMapper.toSpecificDto(budget);
        } catch (Exception e){
            throw new RuntimeException("Error getting budget by id: " + budgetId, e);
        }
    }

    @Transactional
    public BudgetResponseDto createBudget(BudgetRequestDto budgetRequest) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
        UserEntity user = userService.getUser(userId);

        try {
            BudgetEntity newBudget = budgetMapper.toEntity(budgetRequest);
            newBudget.setUser(user);
            BudgetEntity savedBudget = repository.save(newBudget);

            BudgetResponseDto response = budgetMapper.toDto(savedBudget);
            response.setMessage("Budget has been successfully created");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating budget: ", e);
        }
    }

    @Transactional
    public BudgetResponseDto updateBudget(String budgetId, BudgetRequestDto budgetRequest) {
        UUID budgetUuid;
        try {
            budgetUuid = UUID.fromString(budgetId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting budget id to UUID.");
        }


        BudgetEntity updatedBudget;
        try {
            updatedBudget = getBudget(budgetUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Budget with id: " + budgetId + " doesn't exist");
        }

        try {
            if (budgetRequest.getBudgetName() != null) { updatedBudget.setBudgetName(budgetRequest.getBudgetName()); }
            if (budgetRequest.getCategory() != null) { updatedBudget.setCategory(budgetRequest.getCategory()); }
            if (budgetRequest.getBudgetLimit() != null) { updatedBudget.setBudgetLimit(budgetRequest.getBudgetLimit()); }
            if (budgetRequest.getStartDate() != null) { updatedBudget.setStartDate(budgetRequest.getStartDate()); }
            if (budgetRequest.getEndDate() != null) { updatedBudget.setEndDate(budgetRequest.getEndDate()); }
            repository.save(updatedBudget);

            BudgetResponseDto response = budgetMapper.toDto(updatedBudget);
            response.setMessage("Budget has been successfully updated");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error updating budget: ", e);
        }
    }

    @Transactional
    public SuccessResponseDto deleteBudget(String budgetId) {
        UUID budgetUuid;
        try {
            budgetUuid = UUID.fromString(budgetId);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting budget id to UUID.");
        }


        BudgetEntity budget;
        try {
            budget = getBudget(budgetUuid);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Budget with id: " + budgetId + " doesn't exist");
        }

        try {
            repository.delete(budget);

            return SuccessResponseDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.CREATED.value())
                            .message("Budget has been deleted successfully.")
                            .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                            .build();
        } catch (Exception e){
            throw new RuntimeException("Error deleting budget: " + budgetId, e);
        }
    }

    public BudgetEntity getBudget(UUID budgetId) {
        return repository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + budgetId));
    }
}
