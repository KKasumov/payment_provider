package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.*;
import com.kasumov.PaymentProvider.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
public class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;



    @Test
    public void testProcessTopupTransaction() {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .card(Card.builder().cardNumber("1234567890123456").build())
                .customer(Customer.builder().firstName("John").lastName("Doe").country("US").build())
                .paymentMethod("CARD")
                .build();

        StepVerifier.create(transactionService.processTopupTransaction(transaction, "merchantId"))
                .expectNextMatches(savedTransaction -> savedTransaction.getTransactionStatus() == TransactionStatus.SUCCESS)
                .verifyComplete();
    }

    @Test
    public void testProcessPayoutTransaction() {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .card(Card.builder().cardNumber("1234567890123456").build())
                .customer(Customer.builder().firstName("Jane").lastName("Smith").country("CA").build())
                .paymentMethod("CARD")
                .build();

        StepVerifier.create(transactionService.processPayoutTransaction(transaction, "merchantId"))
                .expectNextMatches(savedTransaction -> savedTransaction.getTransactionStatus() == TransactionStatus.SUCCESS)
                .verifyComplete();
    }
}