package org.finance.financemanager.accessibility.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseUpdateResponseDto {
    private String userId;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private Boolean passwordUpdated;
    private String message;
    private String updatedDate;
}
