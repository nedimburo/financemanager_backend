package org.finance.financemanager.accessibility.users.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.finance.financemanager.accessibility.roles.entities.RoleName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private RoleName role;
    private Integer numberOfTransactions;
    private String registrationDate;
}
