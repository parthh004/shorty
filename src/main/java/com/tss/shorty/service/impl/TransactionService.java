package com.tss.shorty.service.impl;

import com.tss.shorty.entity.SystemConfig;
import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.PaymentStatus;
import com.tss.shorty.entity.enums.TransactionAction;
import com.tss.shorty.exception.PaymentFailedException;
import com.tss.shorty.factory.PaymentStrategyFactory;
import com.tss.shorty.payload.response.TransactionRequestDto;
import com.tss.shorty.repository.TransactionRepository;
import com.tss.shorty.repository.UserRepository;
import com.tss.shorty.service.ConfigService;
import com.tss.shorty.service.ITransactionService;
import com.tss.shorty.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService implements ITransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final ConfigService configService;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final UrlService urlService;
    private final UserService userService;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              ConfigService configService,
                              PaymentStrategyFactory paymentStrategyFactory,
                              UrlService urlService,
                              UserService userService,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.configService = configService;
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.urlService = urlService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public Transaction processTransaction(User user, TransactionRequestDto request) {

        // 1. Fetch exact pricing from DB to prevent frontend tampering
        BigDecimal amountToCharge = calculateTransactionAmount(
                TransactionAction.valueOf(request.getAction().toUpperCase()),
                request.getQuantity()
        );

        // 2. Create the INITIATED audit record
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPaymentMethod(request.getPaymentMethod().toUpperCase());
        transaction.setAmount(amountToCharge);
        transaction.setTransactionAction(TransactionAction.valueOf(request.getAction().toUpperCase()));
        transaction.setPaymentStatus(PaymentStatus.INITIATED);
        transaction.setPaymentId("PENDING");

        transaction = transactionRepository.save(transaction);

        try {
            // 3. Dynamically route to UPI, CARD, etc.
            log.info("Payment started with method:{}", request.getPaymentMethod());
            PaymentStrategy strategy = paymentStrategyFactory.getStrategy(request.getPaymentMethod());
            String gatewayPaymentId = strategy.processPayment(amountToCharge, user.getUserId());

            // 4. Mark as SUCCESS if gateway doesn't throw an exception
            transaction.setPaymentId(gatewayPaymentId);
            transaction.setPaymentStatus(PaymentStatus.SUCCESS);
            transaction = transactionRepository.save(transaction);

            // 5. Deliver the purchased digital goods
            executeBusinessAction(user, request);

            return transaction;

        } catch (PaymentFailedException e) {
            // 6. Hard-save the failure state if the gateway rejects the card/UPI
            transaction.setPaymentStatus(PaymentStatus.FAILED);
            transactionRepository.save(transaction);
            throw e; // Rethrow so the controller returns a 400 Bad Request
        }
    }

    private BigDecimal calculateTransactionAmount(TransactionAction action, int quantity) {
        if (action == TransactionAction.RENEWAL) {
            return configService.getPricing().getRenewalFee();
        } else if (action == TransactionAction.SLOT_PURCHASE) {
            BigDecimal pricePerSlot = configService.getPricing().getSlotPrice();
            return pricePerSlot.multiply(BigDecimal.valueOf(quantity));
        }
        throw new IllegalArgumentException("Unknown Action");
    }

    private void executeBusinessAction(User user, TransactionRequestDto request) {
        TransactionAction action = TransactionAction.valueOf(request.getAction().toUpperCase());
        if (action == TransactionAction.RENEWAL) {

            if (request.getUrlId() == null) {
                throw new IllegalArgumentException("urlId is required for URL renewals.");
            }

            int extraVisits = configService.getPricing().getVisitsPerRenewal();
            int extraDays = configService.getPricing().getExpiryDays();

            urlService.renewUrl(request.getUrlId(), user, extraVisits, extraDays);

        } else if (action == TransactionAction.SLOT_PURCHASE) {
            userService.addUrlSlots(user, request.getQuantity());
        }
    }
}
