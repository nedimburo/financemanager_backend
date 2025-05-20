package org.finance.financemanager.common.seeders;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.finance.financemanager.accessibility.roles.entities.RoleEntity;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.roles.repositories.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {

        if (roleRepository.count() > 0) {
            return;
        }

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleName.ADMIN);
        roleRepository.save(adminRole);

        RoleEntity clientRole = new RoleEntity();
        clientRole.setName(RoleName.CLIENT);
        roleRepository.save(clientRole);
    }
}
