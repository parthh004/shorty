package com.tss.shorty.factory;

import com.tss.shorty.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategyMap;

    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.strategyMap = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getSupportedPaymentMethod, Function.identity()));
    }

    public PaymentStrategy getStrategy(String paymentMethod) {
        PaymentStrategy strategy = strategyMap.get(paymentMethod.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method:" + paymentMethod);
        }
        return strategy;
    }
}
