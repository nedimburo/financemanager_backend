package org.finance.financemanager.common.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.enums.FinanceTypes;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.common.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("common/storage")
@Tags(value = {@Tag(name = "Common | Storage"), @Tag(name = OPERATION_ID_NAME + "CommonStorage")})
public class CommonStorageController {

    private final StorageService service;

    @Operation(
            description = "Endpoint used for uploading files or images for all financial types."
    )
    @PostMapping("/upload")
    public SuccessResponseDto uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam FinanceTypes financialType,
            @RequestParam String itemId
    ) throws IOException {
        return service.uploadFile(file, financialType, itemId);
    }

    @Operation(
            description = "Endpoint used for downloading user uploaded files."
    )
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filePath) {
        return service.downloadFile(filePath);
    }

    @Operation(
            description = "Delete a specific file by providing file ID."
    )
    @DeleteMapping("/specific")
    public SuccessResponseDto deleteFile(@RequestParam String fileId) {
        return service.deleteFile(fileId);
    }

    @Operation(
            description = "Delete all files for a financial item by providing financial type and item ID."
    )
    @DeleteMapping("/all")
    public SuccessResponseDto deleteAllFilesForFinancialItem(
        @RequestParam String itemId,
        @RequestParam FinanceTypes financialType
    ) {
        return service.deleteAllFilesForFinancialItem(itemId, financialType);
    }
}
