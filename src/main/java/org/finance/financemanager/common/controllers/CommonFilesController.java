package org.finance.financemanager.common.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.services.FileCreatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;

@Slf4j
@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("common/files")
@Tags(value = {@Tag(name = "Common | Files"), @Tag(name = OPERATION_ID_NAME + "CommonFiles")})
public class CommonFilesController {

    private final FileCreatorService service;

    @PostMapping("/download-csv")
    public ResponseEntity<byte[]> downloadCsv(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ){
        return service.downloadCsv(month, year);
    }

    @PostMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) throws IOException {
        return service.downloadExcel(month, year);
    }
}
