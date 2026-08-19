package com.tss.shorty.service;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import com.tss.shorty.payload.response.PaginatedDto;
import com.tss.shorty.payload.response.TransactionRequestDto;
import com.tss.shorty.payload.response.TransactionResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ITransactionService {
    Transaction processTransaction(User user, @Valid TransactionRequestDto requestDto);

    PaginatedDto<TransactionResponseDto> getAllTransactions(User currentUser, Pageable pageable);
}
