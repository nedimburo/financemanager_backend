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
public class UserProfileResponseDto {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private RoleName role;
    private String registrationDate;
}
