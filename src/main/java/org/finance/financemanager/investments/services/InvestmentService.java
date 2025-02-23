package org.finance.financemanager.investments.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.payloads.DeleteResponseDto;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.finance.financemanager.investments.payloads.*;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.transactions.services.TransactionService;
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
public class InvestmentService {

    private final InvestmentRepository repository;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    public Page<InvestmentResponseDto> getUsersInvestments(Pageable pageable) {
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserId(uid, pageable)
                    .map(investment -> new InvestmentResponseDto(
                            investment.getId(),
                            investment.getUser().getId(),
                            investment.getType().toString(),
                            investment.getInvestmentName(),
                            investment.getAmountInvested(),
                            investment.getCurrentValue(),
                            investment.getInterestRate(),
                            investment.getStartDate().toString(),
                            null,
                            investment.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users investments: ", e);
        }
    }

    @Transactional
    public Page<InvestmentResponseDto> searchUsersInvestments(String investmentName, Pageable pageable){
        try {
            String uid = Auth.getUserId();
            return repository.findAllByUserIdAndInvestmentNameContainingIgnoreCase(uid, investmentName, pageable)
                    .map(investment -> new InvestmentResponseDto(
                            investment.getId(),
                            investment.getUser().getId(),
                            investment.getType().toString(),
                            investment.getInvestmentName(),
                            investment.getAmountInvested(),
                            investment.getCurrentValue(),
                            investment.getInterestRate(),
                            investment.getStartDate().toString(),
                            null,
                            investment.getCreated().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error getting users investments: ", e);
        }
    }

    @Transactional
    public ResponseEntity<InvestmentResponseDto> getInvestmentById(String investmentId) {
        try {
            InvestmentEntity investment = getInvestment(investmentId);
            InvestmentResponseDto response = formatInvestmentResponse(investment);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw new RuntimeException("Error getting investment by id: " + investmentId, e);
        }
    }

    @Transactional
    public ResponseEntity<InvestmentResponseDto> createInvestment(InvestmentRequestDto investmentRequest) {
        try {
            String uid = Auth.getUserId();
            UserEntity user = userService.getUser(uid);

            InvestmentEntity newInvestment = new InvestmentEntity();
            newInvestment.setId(UUID.randomUUID().toString());
            newInvestment.setType(InvestmentType.valueOf(investmentRequest.getType()));
            newInvestment.setInvestmentName(investmentRequest.getInvestmentName());
            newInvestment.setAmountInvested(investmentRequest.getAmountInvested());
            newInvestment.setCurrentValue(investmentRequest.getCurrentValue());
            newInvestment.setInterestRate(investmentRequest.getInterestRate());
            newInvestment.setStartDate(investmentRequest.getStartDate());
            newInvestment.setUser(user);
            InvestmentEntity savedInvestment = repository.save(newInvestment);

            transactionService.createTransactionFromInvestment(savedInvestment);

            InvestmentResponseDto response = formatInvestmentResponse(newInvestment);
            response.setMessage("Investment has been successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating investment: ", e);
        }
    }

    @Transactional
    public ResponseEntity<InvestmentResponseDto> updateInvestment(String investmentId, InvestmentRequestDto investmentRequest) {
        try {
            InvestmentEntity updatedInvestment = getInvestment(investmentId);
            if (investmentRequest.getType() != null) { updatedInvestment.setType(InvestmentType.valueOf(investmentRequest.getType())); }
            if (investmentRequest.getInvestmentName() != null) { updatedInvestment.setInvestmentName(investmentRequest.getInvestmentName()); }
            if (investmentRequest.getAmountInvested() != null) { updatedInvestment.setAmountInvested(investmentRequest.getAmountInvested()); }
            if (investmentRequest.getCurrentValue() != null) { updatedInvestment.setCurrentValue(investmentRequest.getCurrentValue()); }
            if (investmentRequest.getInterestRate() != null) { updatedInvestment.setInterestRate(investmentRequest.getInterestRate()); }
            if (investmentRequest.getStartDate() != null) { updatedInvestment.setStartDate(investmentRequest.getStartDate()); }
            repository.save(updatedInvestment);

            InvestmentResponseDto response = formatInvestmentResponse(updatedInvestment);
            response.setMessage("Investment has been successfully updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error updating investment: ", e);
        }
    }

    @Transactional
    public ResponseEntity<DeleteResponseDto> deleteInvestment(String investmentId) {
        try {
            InvestmentEntity investment = getInvestment(investmentId);
            repository.delete(investment);

            DeleteResponseDto response = new DeleteResponseDto();
            response.setId(investmentId);
            response.setMessage("Investment has been successfully deleted");
            response.setRemovedDate(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
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

    private InvestmentResponseDto formatInvestmentResponse(InvestmentEntity investment) {
        InvestmentResponseDto response = new InvestmentResponseDto();
        response.setInvestmentId(investment.getId());
        response.setUserId(investment.getUser().getId());
        response.setType(investment.getType().toString());
        response.setInvestmentName(investment.getInvestmentName());
        response.setAmountInvested(investment.getAmountInvested() != null ? investment.getAmountInvested() : BigDecimal.ZERO);
        response.setCurrentValue(investment.getCurrentValue() != null ? investment.getCurrentValue() : BigDecimal.ZERO);
        response.setInterestRate(investment.getInterestRate() != null ? investment.getInterestRate() : 0);
        response.setStartDate(investment.getStartDate().toString());
        response.setCreatedDate(investment.getCreated().toString());
        return response;
    }

    public InvestmentEntity getInvestment(String investmentId) {
        return repository.findById(investmentId)
                .orElseThrow(() -> new EntityNotFoundException("Investment not found with id: " + investmentId));
    }
}
