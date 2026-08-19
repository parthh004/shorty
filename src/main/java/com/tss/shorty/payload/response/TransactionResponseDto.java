package com.tss.shorty.payload.response;

import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.PaymentStatus;
import com.tss.shorty.entity.enums.TransactionAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private UUID transactionId;
    private UUID userId;
    private String paymentMethod;
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private TransactionAction transactionAction;
    private LocalDateTime createdAt = LocalDateTime.now();
}
