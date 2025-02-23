package org.finance.financemanager.accessibility.roles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finance.financemanager.accessibility.roles.Role;
import org.finance.financemanager.common.entities.Auditable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "roles")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class RoleEntity extends Auditable implements Role {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
}
