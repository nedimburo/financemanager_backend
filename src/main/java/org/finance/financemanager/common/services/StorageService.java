package org.finance.financemanager.common.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.bill_reminders.entities.BillReminderEntity;
import org.finance.financemanager.bill_reminders.repositories.BillReminderRepository;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.budgets.repositories.BudgetRepository;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.enums.FinanceTypes;
import org.finance.financemanager.common.exceptions.BadRequestException;
import org.finance.financemanager.common.exceptions.ResourceNotFoundException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.files.entities.FileType;
import org.finance.financemanager.files.services.FileService;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.repositories.InvestmentRepository;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.finance.financemanager.savings.repositories.SavingRepository;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final WebClient webClient;
    private final FileService fileService;
    private final InvestmentRepository investmentRepository;
    private final TransactionRepository transactionRepository;
    private final BillReminderRepository billReminderRepository;
    private final BudgetRepository budgetRepository;
    private final SavingRepository savingRepository;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    public SuccessResponseDto uploadFile(MultipartFile file, FinanceTypes financialType, String itemId) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty.");
        }

        UUID itemUuid;

        try {
            itemUuid = UUID.fromString(itemId);
        } catch (Exception e) {
            throw new BadRequestException("Error while converting item id to UUID.");
        }

        String userId;
        try {
            userId = Auth.getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

        String newFilename = this.generateUniqueFileName(file);
        String path = userId + "/" + financialType + "/" + newFilename;
        String publicUrl = this.getPublicUrl(path);
        FileType fileType = this.detectFileType(file);

        FileEntity savedFile = fileService.saveFile(financialType, fileType, publicUrl, itemUuid);
        if (savedFile == null) {
            throw new RuntimeException("Error while saving file.");
        }

        webClient.put()
                .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path)
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                .bodyValue(file.getBytes())
                .retrieve()
                .toBodilessEntity()
                .block();

        return SuccessResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Public URL: " + publicUrl)
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();
    }

    public ResponseEntity<byte[]> downloadFile(String filePath) {
        byte[] downloadedFile = webClient.get()
                .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + filePath)
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(downloadedFile, headers, HttpStatus.OK);
    }

    public SuccessResponseDto deleteFile(String fileId) {
        UUID fileUuid;

        try {
            fileUuid = UUID.fromString(fileId);
        } catch (Exception e) {
            throw new BadRequestException("Error while converting file id to UUID.");
        }

        FileEntity fileForDelete;
        try {
            fileForDelete = fileService.getFile(fileUuid);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("File not found in the database.");
        }

        deleteFileFromSupabase(fileForDelete.getUrl());
        fileService.deleteFile(fileForDelete);

        return SuccessResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("File has been successfully deleted.")
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();
    }

    public SuccessResponseDto deleteAllFilesForFinancialItem (String itemId, FinanceTypes financialType) {
        UUID itemUuid;

        try {
            itemUuid = UUID.fromString(itemId);
        } catch (Exception e) {
            throw new BadRequestException("Error while converting item id to UUID.");
        }

        List<FileEntity> itemFilesForDelete = new ArrayList<>();

        switch (financialType) {
            case INVESTMENTS -> {
                InvestmentEntity investment = investmentRepository.findById(itemUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Investment not found."));
                itemFilesForDelete.addAll(investment.getFiles());
                investment.getFiles().clear();
                investmentRepository.save(investment);
            }
            case TRANSACTIONS -> {
                TransactionEntity transaction = transactionRepository.findById(itemUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
                itemFilesForDelete.addAll(transaction.getFiles());
                transaction.getFiles().clear();
                transactionRepository.save(transaction);
            }
            case BILL_REMINDERS -> {
                BillReminderEntity billReminder = billReminderRepository.findById(itemUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Bill reminder not found"));
                itemFilesForDelete.addAll(billReminder.getFiles());
                billReminder.getFiles().clear();
                billReminderRepository.save(billReminder);
            }
            case SAVINGS -> {
                SavingEntity saving = savingRepository.findById(itemUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Saving not found"));
                itemFilesForDelete.addAll(saving.getFiles());
                saving.getFiles().clear();
                savingRepository.save(saving);
            }
            case BUDGETS -> {
                BudgetEntity budget = budgetRepository.findById(itemUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
                itemFilesForDelete.addAll(budget.getFiles());
                budget.getFiles().clear();
                budgetRepository.save(budget);
            }
            default -> throw new IllegalArgumentException("Invalid financial type");
        }

        for (FileEntity file : itemFilesForDelete) {
            deleteFileFromSupabase(file.getUrl());
            fileService.deleteFile(file);
        }

        return SuccessResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("All files for financial the chose item have been deleted.")
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();
    }

    private String getPublicUrl(String filePath) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filePath;
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String randomUUID = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + randomUUID + extension;
    }

    private FileType detectFileType(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("File name is missing");
        }

        String extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

        if (List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
            return FileType.IMAGE;
        } else {
            return FileType.DOCUMENT;
        }
    }

    public void deleteFileFromSupabase(String fileUrl) {
        webClient.delete()
                .uri(fileUrl)
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
