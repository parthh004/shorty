package com.tss.shorty.strategy;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentStrategy {
    String getSupportedPaymentMethod();

    String processPayment(BigDecimal amount, UUID userId);
}
