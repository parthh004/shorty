package com.tss.shorty.controller;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import com.tss.shorty.payload.response.TransactionRequestDto;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final ITransactionService transactionService;
    private final CurrentUserProvider currentUserProvider;

    public TransactionController(ITransactionService transactionService, CurrentUserProvider currentUserProvider) {
        this.transactionService = transactionService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/process")
    public ResponseEntity<Transaction> processPayment(
            @Valid @RequestBody TransactionRequestDto requestDto) {

        User currentUser = currentUserProvider.get();
        // Let the service handle the heavy lifting and pricing logic
        Transaction completedTransaction = transactionService.processTransaction(
                currentUser,
                requestDto
        );

        return ResponseEntity.ok(completedTransaction);
    }
}
