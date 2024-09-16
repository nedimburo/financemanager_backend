package org.finance.financemanager.budgets.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.payloads.BudgetRequestDto;
import org.finance.financemanager.budgets.payloads.BudgetResponseDto;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository repository;
    private final UserService userService;

    @Transactional
    public Page<BudgetResponseDto> getUsersBudgets(Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserId(uid, pageable)
                    .map(budget -> new BudgetResponseDto(
                            budget.getId(),
                            budget.getUser().getId(),
                            budget.getBudgetName(),
                            budget.getCategory().toString(),
                            budget.getBudgetLimit(),
                            budget.getStartDate().toString(),
                            budget.getEndDate().toString(),
                            null,
                            budget.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users budgets: ", e);
        }
    }

    @Transactional
    public Page<BudgetResponseDto> searchUsersBudgets(String budgetName, Pageable pageable){
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserIdAndBudgetNameIsContainingIgnoreCase(uid, budgetName, pageable)
                    .map(budget -> new BudgetResponseDto(
                            budget.getId(),
                            budget.getUser().getId(),
                            budget.getBudgetName(),
                            budget.getCategory().toString(),
                            budget.getBudgetLimit(),
                            budget.getStartDate().toString(),
                            budget.getEndDate().toString(),
                            null,
                            budget.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users budgets: ", e);
        }
    }

    @Transactional
    public ResponseEntity<BudgetResponseDto> getBudgetById(String budgetId) {
        try {
            BudgetEntity budget = getBudget(budgetId);
            BudgetResponseDto response = formatBudgetResponse(budget);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error getting budget by id: " + budgetId, e);
        }
    }

    @Transactional
    public ResponseEntity<BudgetResponseDto> createBudget(BudgetRequestDto budgetRequest) {
        try {
            String uid = Auth.getUserId();
            UserEntity user = userService.getUser(uid);

            BudgetEntity newBudget = new BudgetEntity();
            newBudget.setId(UUID.randomUUID().toString());
            newBudget.setBudgetName(budgetRequest.getBudgetName());
            newBudget.setCategory(FinanceCategory.valueOf(budgetRequest.getCategory()));
            newBudget.setBudgetLimit(budgetRequest.getBudgetLimit());
            newBudget.setStartDate(budgetRequest.getStartDate());
            newBudget.setEndDate(budgetRequest.getEndDate());
            newBudget.setCreated(LocalDateTime.now());
            newBudget.setUpdated(LocalDateTime.now());
            newBudget.setUser(user);
            repository.save(newBudget);

            BudgetResponseDto response = formatBudgetResponse(newBudget);
            response.setMessage("Budget has been successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating budget: ", e);
        }
    }

    @Transactional
    public ResponseEntity<BudgetResponseDto> updateBudget(String budgetId, BudgetRequestDto budgetRequest) {
        try {
            BudgetEntity updatedBudget = getBudget(budgetId);
            if (budgetRequest.getBudgetName() != null) { updatedBudget.setBudgetName(budgetRequest.getBudgetName()); }
            if (budgetRequest.getCategory() != null) { updatedBudget.setCategory(FinanceCategory.valueOf(budgetRequest.getCategory())); }
            if (budgetRequest.getBudgetLimit() != null) { updatedBudget.setBudgetLimit(budgetRequest.getBudgetLimit()); }
            if (budgetRequest.getStartDate() != null) { updatedBudget.setStartDate(budgetRequest.getStartDate()); }
            if (budgetRequest.getEndDate() != null) { updatedBudget.setEndDate(budgetRequest.getEndDate()); }
            updatedBudget.setUpdated(LocalDateTime.now());
            repository.save(updatedBudget);

            BudgetResponseDto response = formatBudgetResponse(updatedBudget);
            response.setMessage("Budget has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating budget: ", e);
        }
    }

    @Transactional
    public ResponseEntity<DeleteResponseDto> deleteBudget(String budgetId) {
        try {
            BudgetEntity budget = getBudget(budgetId);
            repository.delete(budget);

            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(budgetId);
            response.setMessage("Budget has been successfully deleted");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error deleting budget: " + budgetId, e);
        }
    }

    private BudgetResponseDto formatBudgetResponse(BudgetEntity budget) {
        BudgetResponseDto response = new BudgetResponseDto();
        response.setBudgetId(budget.getId());
        response.setUserId(budget.getUser().getId());
        response.setBudgetName(budget.getBudgetName());
        response.setCategory(budget.getCategory().toString());
        response.setBudgetLimit(budget.getBudgetLimit());
        response.setStartDate(budget.getStartDate().toString());
        response.setEndDate(budget.getEndDate().toString());
        response.setCreatedDate(budget.getCreated().toString());
        return response;
    }

    public BudgetEntity getBudget(String budgetId) {
        return repository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + budgetId));
    }
}
