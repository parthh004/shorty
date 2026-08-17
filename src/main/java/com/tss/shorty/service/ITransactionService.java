package com.tss.shorty.service;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import com.tss.shorty.payload.response.TransactionRequestDto;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ITransactionService {
    Transaction processTransaction(User user, @Valid TransactionRequestDto requestDto);
}
