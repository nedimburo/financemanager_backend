package org.finance.financemanager.files.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.common.enums.FinanceTypes;
import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.files.entities.FileType;
import org.finance.financemanager.files.repositories.FileRepository;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository repository;
    private final InvestmentRepository investmentRepository;
    private final TransactionRepository transactionRepository;
    private final BillReminderRepository billReminderRepository;
    private final SavingRepository savingRepository;
    private final BudgetRepository budgetRepository;

    @Transactional
    public FileEntity saveFile(FinanceTypes financialType, FileType fileType, String url, UUID itemUuid) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileType(fileType);
        fileEntity.setUrl(url);

        switch (financialType) {
            case FinanceTypes.INVESTMENTS -> fileEntity.setInvestment(investmentRepository.getReferenceById(itemUuid));
            case FinanceTypes.TRANSACTIONS -> fileEntity.setTransaction(transactionRepository.getReferenceById(itemUuid));
            case FinanceTypes.BILL_REMINDERS -> fileEntity.setBillReminder(billReminderRepository.getReferenceById(itemUuid));
            case FinanceTypes.SAVINGS -> fileEntity.setSaving(savingRepository.getReferenceById(itemUuid));
            case FinanceTypes.BUDGETS -> fileEntity.setBudget(budgetRepository.getReferenceById(itemUuid));
            default -> throw new IllegalArgumentException("Invalid financial type");
        }

        return repository.save(fileEntity);
    }

    public FileEntity getFile(UUID fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with id: " + fileId));
    }

}
