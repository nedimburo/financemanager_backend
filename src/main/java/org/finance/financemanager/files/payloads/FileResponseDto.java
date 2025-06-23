package org.finance.financemanager.files.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.files.entities.FileType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {
    private String fileId;
    private String url;
    private FileType fileType;
    private String createdDate;
}
