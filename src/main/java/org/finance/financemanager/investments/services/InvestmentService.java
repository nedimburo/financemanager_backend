package org.finance.financemanager.investments.services;

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
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.finance.financemanager.investments.mappers.InvestmentMapper;
import org.finance.financemanager.investments.payloads.*;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.investments.specifications.InvestmentSpecification;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository repository;
    private final InvestmentMapper investmentMapper;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    public Page<InvestmentResponseDto> getUsersInvestments(Pageable pageable, String query, InvestmentType type) {
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
            Specification<InvestmentEntity> spec = InvestmentSpecification.filterInvestments(query, type, userId);
            return repository.findAll(spec, pageable).map(investmentMapper::toDto);
        } catch (Exception e) {
            throw new RuntimeException("Error getting users investments: ", e);
        }
    }

    @Transactional
    public InvestmentResponseDto getInvestmentById(String investmentId) {
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
        InvestmentEntity investment = getInvestment(investmentId);

        try {
            return investmentMapper.toDto(investment);
        } catch (Exception e){
            throw new RuntimeException("Error getting investment by id: " + investmentId, e);
        }
    }

    @Transactional
    public InvestmentResponseDto createInvestment(InvestmentRequestDto investmentRequest) {
        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        UserEntity user = userService.getUser(userId);

        try {
            InvestmentEntity newInvestment = investmentMapper.toEntity(investmentRequest);
            newInvestment.setId(UUID.randomUUID().toString());
            newInvestment.setUser(user);
            InvestmentEntity savedInvestment = repository.save(newInvestment);

            transactionService.createTransactionFromInvestment(savedInvestment);

            InvestmentResponseDto response = investmentMapper.toDto(savedInvestment);
            response.setMessage("Investment has been successfully created");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating investment: ", e);
        }
    }

    @Transactional
    public InvestmentResponseDto updateInvestment(String investmentId, InvestmentRequestDto investmentRequest) {
        try {
            InvestmentEntity updatedInvestment = getInvestment(investmentId);
            if (investmentRequest.getType() != null) { updatedInvestment.setType(investmentRequest.getType()); }
            if (investmentRequest.getInvestmentName() != null) { updatedInvestment.setInvestmentName(investmentRequest.getInvestmentName()); }
            if (investmentRequest.getAmountInvested() != null) { updatedInvestment.setAmountInvested(investmentRequest.getAmountInvested()); }
            if (investmentRequest.getCurrentValue() != null) { updatedInvestment.setCurrentValue(investmentRequest.getCurrentValue()); }
            if (investmentRequest.getInterestRate() != null) { updatedInvestment.setInterestRate(investmentRequest.getInterestRate()); }
            if (investmentRequest.getStartDate() != null) { updatedInvestment.setStartDate(investmentRequest.getStartDate()); }
            repository.save(updatedInvestment);

            InvestmentResponseDto response = investmentMapper.toDto(updatedInvestment);
            response.setMessage("Investment has been successfully updated");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error updating investment: ", e);
        }
    }

    @Transactional
    public ResponseEntity<SuccessResponseDto> deleteInvestment(String investmentId) {
        try {
            InvestmentEntity investment = getInvestment(investmentId);
            repository.delete(investment);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(SuccessResponseDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.CREATED.value())
                            .message("Investment has been deleted successfully.")
                            .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                            .build());
        } catch (Exception e){
            throw new RuntimeException("Error deleting investment: " + investmentId, e);
        }
    }

    @Transactional
    public ResponseEntity<InvestmentDetailsResponseDto> getInvestmentDetails() {
        try {
            String uid = Auth.getUserId();
            BigDecimal totalAmountInvested = repository.findInvestmentAmountInvestedTotalByUserId(uid);
            BigDecimal totalCurrentValue = repository.findInvestmentCurrentValueTotalByUserId(uid);
            InvestmentEntity highInvestedAmount = repository.findHighestInvestmentAmountInvestedByUserId(uid);
            InvestmentEntity highCurrentValue = repository.findHighestInvestmentCurrentValueByUserId(uid);
            InvestmentDetailsResponseDto response = new InvestmentDetailsResponseDto();
            response.setTotalAmountInvested(totalAmountInvested);
            response.setTotalCurrentValue(totalCurrentValue);
            response.setInvestmentHighInvestedName(highInvestedAmount != null ? highInvestedAmount.getInvestmentName() : "N/A");
            response.setInvestmentHighInvestedAmount(highInvestedAmount != null ? highInvestedAmount.getAmountInvested() : BigDecimal.ZERO);
            response.setInvestmentHighInvestedValue(highInvestedAmount != null ? highInvestedAmount.getCurrentValue() : BigDecimal.ZERO);
            response.setInvestmentHighCurrentName(highCurrentValue != null ? highCurrentValue.getInvestmentName() : "N/A");
            response.setInvestmentHighCurrentAmount(highCurrentValue != null ? highCurrentValue.getAmountInvested() : BigDecimal.ZERO);
            response.setInvestmentHighCurrentValue(highCurrentValue != null ? highCurrentValue.getCurrentValue() : BigDecimal.ZERO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting investment details: " + e);
        }
    }

    @Transactional
    public ResponseEntity<InvestmentValueResponseDto> editInvestmentValue(String investmentId, InvestmentValueRequestDto investmentValueRequest) {
        try {
            InvestmentEntity updatedInvestment = getInvestment(investmentId);
            if (investmentValueRequest.getAmountInvested() != null) { updatedInvestment.setAmountInvested(investmentValueRequest.getAmountInvested()); }
            if (investmentValueRequest.getCurrentValue() != null) { updatedInvestment.setCurrentValue(investmentValueRequest.getCurrentValue()); }
            if (investmentValueRequest.getInterestRate() != null) { updatedInvestment.setInterestRate(investmentValueRequest.getInterestRate()); }
            InvestmentValueResponseDto response = new InvestmentValueResponseDto();
            response.setAmountInvested(updatedInvestment.getAmountInvested());
            response.setCurrentValue(updatedInvestment.getCurrentValue());
            response.setInterestRate(updatedInvestment.getInterestRate());
            response.setMessage("Investment has been successfully updated");
            response.setUpdatedDate(LocalDateTime.now().toString());
            repository.save(updatedInvestment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating investment values: ", e);
        }
    }

    public InvestmentEntity getInvestment(String investmentId) {
        return repository.findById(investmentId)
                .orElseThrow(() -> new EntityNotFoundException("Investment not found with id: " + investmentId));
    }
}
