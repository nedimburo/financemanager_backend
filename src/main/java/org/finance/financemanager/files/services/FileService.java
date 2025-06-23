package org.finance.financemanager.files.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.common.enums.FinanceTypes;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.files.entities.FileType;
import org.finance.financemanager.files.repositories.FileRepository;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.finance.financemanager.transactions.entities.TransactionEntity;
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

        FileEntity savedFile = repository.save(fileEntity);

        switch (financialType) {
            case FinanceTypes.INVESTMENTS -> {
                InvestmentEntity investment = investmentRepository.getReferenceById(itemUuid);
                investment.getFiles().add(fileEntity);
                investmentRepository.save(investment);
            }
            case FinanceTypes.TRANSACTIONS -> {
                TransactionEntity transaction = transactionRepository.getReferenceById(itemUuid);
                transaction.getFiles().add(fileEntity);
                transactionRepository.save(transaction);
            }
            case FinanceTypes.BILL_REMINDERS -> {
                BillReminderEntity billReminder = billReminderRepository.getReferenceById(itemUuid);
                billReminder.getFiles().add(fileEntity);
                billReminderRepository.save(billReminder);
            }
            case FinanceTypes.SAVINGS -> {
                SavingEntity saving = savingRepository.getReferenceById(itemUuid);
                saving.getFiles().add(fileEntity);
                savingRepository.save(saving);
            }
            case FinanceTypes.BUDGETS -> {
                BudgetEntity budget = budgetRepository.getReferenceById(itemUuid);
                budget.getFiles().add(fileEntity);
                budgetRepository.save(budget);
            }
            default -> throw new IllegalArgumentException("Invalid financial type");
        }

        return savedFile;
    }

    @Transactional
    public void deleteFile(FileEntity fileForDelete) {
        repository.delete(fileForDelete);
    }

    public FileEntity getFile(UUID fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with id: " + fileId));
    }

}
