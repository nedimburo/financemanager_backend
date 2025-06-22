package org.finance.financemanager.common.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.config.Auth;
import org.finance.financemanager.common.enums.FinanceTypes;
import org.finance.financemanager.common.exceptions.BadRequestException;
import org.finance.financemanager.common.exceptions.UnauthorizedException;
import org.finance.financemanager.common.payloads.SuccessResponseDto;
import org.finance.financemanager.files.entities.FileEntity;
import org.finance.financemanager.files.entities.FileType;
import org.finance.financemanager.files.services.FileService;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final WebClient webClient;
    private final FileService fileService;

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
}
