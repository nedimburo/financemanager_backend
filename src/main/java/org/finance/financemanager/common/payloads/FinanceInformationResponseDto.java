package org.finance.financemanager.common.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.bill_reminders.payloads.BillReminderDetailsResponseDto;
import org.finance.financemanager.budgets.payloads.BudgetDetailsResponseDto;
import org.finance.financemanager.investments.payloads.InvestmentDetailsResponseDto;
import org.finance.financemanager.savings.payloads.SavingDetailsResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionDetailsResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinanceInformationResponseDto {
    private BillReminderDetailsResponseDto billReminderDetails;
    private BudgetDetailsResponseDto budgetDetails;
    private InvestmentDetailsResponseDto investmentDetails;
    private SavingDetailsResponseDto savingDetails;
    private TransactionDetailsResponseDto transactionDetails;
}
