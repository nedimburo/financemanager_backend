package org.finance.financemanager.accessibility.users.mappers;

import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.finance.financemanager.accessibility.users.payloads.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "userId"),
            @Mapping(source = "created", target = "registrationDate"),
            @Mapping(target = "role", expression = "java(getRole(userEntity))"),
            @Mapping(target = "numberOfTransactions", expression = "java(getTransactionCount(userEntity))")
    })
    UserResponseDto toDto(UserEntity userEntity);

    default RoleName getRole(UserEntity user) {
        return user.getRole().getName();
    }

    default Integer getTransactionCount(UserEntity user) {
        if (user.getTransactions() == null) {
            return 0;
        }
        return user.getTransactions().size();
    }
}
