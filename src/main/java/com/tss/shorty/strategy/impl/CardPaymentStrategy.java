package com.tss.shorty.strategy.impl;

import com.tss.shorty.exception.PaymentFailedException;
import com.tss.shorty.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
public class CardPaymentStrategy implements PaymentStrategy {
    private static final Logger log = LoggerFactory.getLogger(CardPaymentStrategy.class);

    @Override
    public String getSupportedPaymentMethod() {
        return "CARD";
    }

    @Override
    public String processPayment(BigDecimal amount, UUID userId) {
        try {
            // Call your Card gateway API (Stripe, Braintree, etc.)
            log.info("Processing Credit Card payment of " + amount);

            Random random = new Random();
            int result = random.nextInt(1, 11);
            if (result > 7) {
                throw new PaymentFailedException("UPI Pin Incorrect or Insufficient Funds.");
            }

            //Return the generated transaction/payment ID
            return "ch_card_" + UUID.randomUUID().toString().substring(0, 8);
        } catch (Exception e) {
            throw new PaymentFailedException("Payment gateway unreachable:" + e.getMessage());
        }
    }
}
