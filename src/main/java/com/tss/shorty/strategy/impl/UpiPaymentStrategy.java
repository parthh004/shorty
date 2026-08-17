package com.tss.shorty.strategy.impl;

import com.tss.shorty.exception.PaymentFailedException;
import com.tss.shorty.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Service
public class UpiPaymentStrategy implements PaymentStrategy {
    private static final Logger log = LoggerFactory.getLogger(UpiPaymentStrategy.class);

    @Override
    public String getSupportedPaymentMethod() {
        return "UPI";
    }

    @Override
    public String processPayment(BigDecimal amount, UUID userId) {
        try {
            // Call your UPI gateway API
            log.info("Processing UPI payment of " + amount);

            Random random = new Random();
            int result = random.nextInt(1, 11);
            if (result > 7) {
                throw new PaymentFailedException("UPI Pin Incorrect or Insufficient Funds.");
            }

            // Return the generated transaction/payment ID from the gateway
            return "txn_upi_" + UUID.randomUUID().toString().substring(0, 8);
        } catch (Exception e) {
            throw new PaymentFailedException("Payment gateway unreachable:" + e.getMessage());
        }
    }
}
