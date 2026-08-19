package com.tss.shorty.controller;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import com.tss.shorty.payload.response.PaginatedDto;
import com.tss.shorty.payload.response.TransactionRequestDto;
import com.tss.shorty.payload.response.TransactionResponseDto;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<PaginatedDto<TransactionResponseDto>> getAllTransactions(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        User user = currentUserProvider.get();
        PaginatedDto<TransactionResponseDto> paginatedDto = transactionService.getAllTransactions(user, pageable);
        return new ResponseEntity<>(paginatedDto, HttpStatus.OK);
    }
}
