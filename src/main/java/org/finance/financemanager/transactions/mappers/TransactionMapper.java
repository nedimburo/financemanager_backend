package org.finance.financemanager.transactions.mappers;

import org.finance.financemanager.files.mappers.FileMapper;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.payloads.TransactionRequestDto;
import org.finance.financemanager.transactions.payloads.TransactionResponseDto;
import org.finance.financemanager.transactions.payloads.TransactionSpecificResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FileMapper.class)
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "transactionId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate")
    })
    TransactionResponseDto toDto(TransactionEntity transaction);

    @Mappings({
            @Mapping(source = "id", target = "transactionId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "created", target = "createdDate"),
            @Mapping(source = "updated", target = "updatedDate"),
            @Mapping(source = "files", target = "files")
    })
    TransactionSpecificResponseDto toSpecificDto(TransactionEntity transaction);

    TransactionEntity toEntity(TransactionRequestDto request);
}
