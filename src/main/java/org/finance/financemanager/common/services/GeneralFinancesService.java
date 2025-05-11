package org.finance.financemanager.common.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.accessibility.users.services.UserService;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.payloads.BillReminderDetailsResponseDto;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.payloads.BudgetDetailsResponseDto;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.FinanceInformationResponseDto;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.payloads.InvestmentDetailsResponseDto;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.payloads.SavingDetailsResponseDto;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.finance.financemanager.transactions.payloads.TransactionDetailsResponseDto;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class GeneralFinancesService {

    private final BillReminderRepository billReminderRepository;
    private final BudgetRepository budgetRepository;
    private final InvestmentRepository investmentRepository;
    private final SavingRepository savingRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Transactional
    public FinanceInformationResponseDto getGeneralFinances() {
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
            FinanceInformationResponseDto response = new FinanceInformationResponseDto();
            response.setBillReminderDetails(getBillRemindersDetails(userId));
            response.setBudgetDetails(getBudgetDetails(userId));
            response.setInvestmentDetails(getInvestmentDetails(userId));
            response.setSavingDetails(getSavingDetails(userId));
            response.setTransactionDetails(getTransactionDetails(userId));
            return response;
        } catch (Exception e) {
            throw new UnauthorizedException("Error while trying to fetch users general finance information: " + e.getMessage());
        }
    }

    @Transactional
    public BillReminderDetailsResponseDto getBillRemindersDetails(String userId) {
        try {
            BigDecimal totalPaidAmount = billReminderRepository.findTotalPaidBillsByUserId(userId);
            BigDecimal totalUnpaidAmount = billReminderRepository.findTotalUnpaidBillsByUserId(userId);
            Optional<BillReminderEntity> optionalBillReminder = billReminderRepository.findFirstByUserIdAndIsPaidFalseOrderByDueDateAsc(userId);
            BillReminderDetailsResponseDto response = new BillReminderDetailsResponseDto();
            response.setTotalAmountPaidBills(totalPaidAmount != null ? totalPaidAmount : BigDecimal.ZERO);
            response.setTotalAmountUnpaidBills(totalUnpaidAmount != null ? totalUnpaidAmount : BigDecimal.ZERO);
            if (optionalBillReminder.isPresent()) {
                BillReminderEntity billReminderToPay = optionalBillReminder.get();
                response.setBillToPayName(billReminderToPay.getBillName());
                response.setBillToPayAmount(billReminderToPay.getAmount());
                response.setBillToPayDueDate(billReminderToPay.getDueDate().toString());
            } else {
                response.setBillToPayName("N/A");
                response.setBillToPayAmount(BigDecimal.ZERO);
                response.setBillToPayDueDate("N/A");
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error getting bill reminder details: " + e.getMessage());
        }
    }

    @Transactional
    public BudgetDetailsResponseDto getBudgetDetails(String userId) {
        try {
            BudgetEntity biggestBudget = budgetRepository.findBiggestBudgetByUserId(userId);
            BudgetEntity lowestBudget = budgetRepository.findLowestBudgetByUserId(userId);
            BudgetDetailsResponseDto response = new BudgetDetailsResponseDto();
            response.setBiggestBudgetName(biggestBudget != null ? biggestBudget.getBudgetName() : "N/A");
            response.setBiggestBudgetAmount(biggestBudget != null ? biggestBudget.getBudgetLimit() : BigDecimal.ZERO);
            response.setLowestBudgetName(lowestBudget != null ? lowestBudget.getBudgetName() : "N/A");
            response.setLowestBudgetAmount(lowestBudget != null ? lowestBudget.getBudgetLimit() : BigDecimal.ZERO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting budget details: " + e.getMessage());
        }
    }

    @Transactional
    public InvestmentDetailsResponseDto getInvestmentDetails(String userId) {
        try {
            BigDecimal totalAmountInvested = investmentRepository.findInvestmentAmountInvestedTotalByUserId(userId);
            BigDecimal totalCurrentValue = investmentRepository.findInvestmentCurrentValueTotalByUserId(userId);
            InvestmentEntity highInvestedAmount = investmentRepository.findHighestInvestmentAmountInvestedByUserId(userId);
            InvestmentEntity highCurrentValue = investmentRepository.findHighestInvestmentCurrentValueByUserId(userId);
            InvestmentDetailsResponseDto response = new InvestmentDetailsResponseDto();
            response.setTotalAmountInvested(totalAmountInvested);
            response.setTotalCurrentValue(totalCurrentValue);
            response.setInvestmentHighInvestedName(highInvestedAmount != null ? highInvestedAmount.getInvestmentName() : "N/A");
            response.setInvestmentHighInvestedAmount(highInvestedAmount != null ? highInvestedAmount.getAmountInvested() : BigDecimal.ZERO);
            response.setInvestmentHighInvestedValue(highInvestedAmount != null ? highInvestedAmount.getCurrentValue() : BigDecimal.ZERO);
            response.setInvestmentHighCurrentName(highCurrentValue != null ? highCurrentValue.getInvestmentName() : "N/A");
            response.setInvestmentHighCurrentAmount(highCurrentValue != null ? highCurrentValue.getAmountInvested() : BigDecimal.ZERO);
            response.setInvestmentHighCurrentValue(highCurrentValue != null ? highCurrentValue.getCurrentValue() : BigDecimal.ZERO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting investment details: " + e.getMessage());
        }
    }

    @Transactional
    public SavingDetailsResponseDto getSavingDetails(String userId) {
        try {
            SavingEntity closestSaving = savingRepository.findSavingWithSmallestDifferenceByUserId(userId);
            SavingEntity furthestSaving = savingRepository.findSavingWithBiggestDifferenceByUserId(userId);
            SavingDetailsResponseDto response = new SavingDetailsResponseDto();
            response.setClosestSavingGoalName(closestSaving != null ? closestSaving.getGoalName() : "N/A");
            response.setClosestSavingCurrentAmount(closestSaving != null ? closestSaving.getCurrentAmount() : BigDecimal.ZERO);
            response.setClosestSavingTargetAmount(closestSaving != null ? closestSaving.getTargetAmount() : BigDecimal.ZERO);
            response.setFurthestSavingGoalName(furthestSaving != null ? furthestSaving.getGoalName() : "N/A");
            response.setFurthestSavingCurrentAmount(furthestSaving != null ? furthestSaving.getCurrentAmount() : BigDecimal.ZERO);
            response.setFurthestSavingTargetAmount(furthestSaving != null ? furthestSaving.getTargetAmount() : BigDecimal.ZERO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting savings details: " + e.getMessage());
        }
    }

    @Transactional
    public TransactionDetailsResponseDto getTransactionDetails(String userId) {
        try {
            BigDecimal totalExpense = transactionRepository.findTotalExpenseByUserId(userId);
            BigDecimal totalIncome = transactionRepository.findTotalIncomeByUserId(userId);
            Long noOfExpense = transactionRepository.countExpensesByUserId(userId);
            Long noOfIncome = transactionRepository.countIncomeByUserId(userId);
            BigDecimal maxExpenseAmount = transactionRepository.findMaxExpenseByUserId(userId);
            BigDecimal maxIncomeAmount = transactionRepository.findMaxIncomeByUserId(userId);
            TransactionDetailsResponseDto response = new TransactionDetailsResponseDto();
            response.setExpenseAmount(totalExpense != null ? totalExpense : BigDecimal.ZERO);
            response.setIncomeAmount(totalIncome != null ? totalIncome : BigDecimal.ZERO);
            response.setNoOfExpenses(noOfExpense != null ? noOfExpense : 0);
            response.setNoOfIncomes(noOfIncome != null ? noOfIncome : 0);
            response.setHighestExpenseAmount(maxExpenseAmount != null ? maxExpenseAmount : BigDecimal.ZERO);
            response.setHighestIncomeAmount(maxIncomeAmount != null ? maxIncomeAmount : BigDecimal.ZERO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error getting transaction details: " + e.getMessage());
        }
    }
}
