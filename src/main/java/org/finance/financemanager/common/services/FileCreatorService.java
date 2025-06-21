package org.finance.financemanager.common.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.services.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class FileCreatorService {

    private final TransactionService transactionService;

    @Transactional
    public ResponseEntity<byte[]> downloadCsv(Integer month, Integer year, LocalDate startDate, LocalDate endDate) {
        List<TransactionResponseDto> selectedTransactions = transactionService.getFilteredTransactions(month, year, startDate, endDate);
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Description,Type,Category,Amount,Date of transactions,Saved date\n");
        for (TransactionResponseDto transaction : selectedTransactions) {
            csvContent.append(String.join(",",
                    transaction.getDescription(),
                    transaction.getType().toString(),
                    transaction.getCategory().toString(),
                    transaction.getAmount().toString() + "€",
                    transaction.getDate(),
                    transaction.getCreatedDate()
                    ));
            csvContent.append("\n");
        }

        byte[] csvBytes = csvContent.toString().getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProcessedAddresses.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<byte[]> downloadExcel(Integer month, Integer year, LocalDate startDate, LocalDate endDate) throws IOException {
        List<TransactionResponseDto> selectedTransactions = transactionService.getFilteredTransactions(month, year, startDate, endDate);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row headerRow = sheet.createRow(0);
        String[] headerData = {"Description", "Type", "Category", "Amount", "Date of transactions", "Saved date"};

        for (int i = 0; i < headerData.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerData[i]);
        }

        int rowNum = 1;
        for (TransactionResponseDto transaction : selectedTransactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getDescription());
            row.createCell(1).setCellValue(transaction.getType().toString());
            row.createCell(2).setCellValue(transaction.getCategory().toString());
            row.createCell(3).setCellValue(transaction.getAmount().toString() + "€");
            row.createCell(4).setCellValue(transaction.getDate());
            row.createCell(5).setCellValue(transaction.getCreatedDate());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        byte[] excelBytes = out.toByteArray();
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TransactionsReport.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
