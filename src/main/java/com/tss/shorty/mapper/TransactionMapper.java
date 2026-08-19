package com.tss.shorty.mapper;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.enums.TransactionAction;
import com.tss.shorty.payload.response.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "user.userId", target = "userId")
    TransactionResponseDto mapTo(Transaction transaction);
}
