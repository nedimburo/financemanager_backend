package org.finance.financemanager.accessibility.roles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.roles.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "roles")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class RoleEntity implements Role {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;
}
