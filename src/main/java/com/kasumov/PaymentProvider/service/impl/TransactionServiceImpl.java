package com.kasumov.PaymentProvider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasumov.PaymentProvider.dto.webhook.WebhookDto;
import com.kasumov.PaymentProvider.exception.RequestPayoutTransactionInvalidAmountException;
import com.kasumov.PaymentProvider.exception.RequestTopUpTransactionInvalidPaymentMethodException;
import com.kasumov.PaymentProvider.mapper.CardMapper;
import com.kasumov.PaymentProvider.mapper.CustomerMapper;
import com.kasumov.PaymentProvider.model.*;
import com.kasumov.PaymentProvider.repository.TransactionRepository;
import com.kasumov.PaymentProvider.repository.WebhookRepository;
import com.kasumov.PaymentProvider.service.AccountService;
import com.kasumov.PaymentProvider.service.CardService;
import com.kasumov.PaymentProvider.service.CustomerService;
import com.kasumov.PaymentProvider.service.TransactionService;
import com.kasumov.PaymentProvider.webhook.WebhookNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final CardMapper cardMapper;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final AccountService accountService;
    private final CustomerService customerService;
    private final WebhookNotificationService webhookNotificationService;
    private final WebhookRepository webhookRepository;


    @Override
    public Flux<Transaction> getAllTransactionsForMerchantByTypeAndPeriod
            (TransactionType type, LocalDateTime startDate, LocalDateTime endDate, String merchantId) {
        return accountService.getAllAccountsForMerchant(merchantId)
                .flatMap(account -> transactionRepository.findAllTransactionsForAccountByTypeAndPeriod
                        (type, startDate, endDate, account.getId()))
                .flatMap(transactionEntity ->
                        Mono.zip(
                                cardService.getById(transactionEntity.getCardNumber()),
                                customerService.getById(transactionEntity.getCardNumber()),
                                Mono.just(transactionEntity)
                        ).map(tuple -> {
                            Card card = tuple.getT1();
                            Customer customer = tuple.getT2();
                            Transaction transaction = tuple.getT3();

                            transaction.setCard(card);
                            transaction.setCustomer(customer);
                            return transaction;
                        })
                );
    }

    @Override
    public Flux<Transaction> getAllTransactionsForMerchantByTypeAndDay(TransactionType type, LocalDate date, String merchantId) {
        return accountService.getAllAccountsForMerchant(merchantId)
                .flatMap(account -> transactionRepository.findAllTransactionsForAccountByTypeAndDay(type, date, account.getId()))
                .flatMap(transactionEntity ->
                        Mono.zip(
                                cardService.getById(transactionEntity.getCardNumber()),
                                customerService.getById(transactionEntity.getCardNumber()),
                                Mono.just(transactionEntity)
                        ).map(tuple -> {
                            Card card = tuple.getT1();
                            Customer customer = tuple.getT2();
                            Transaction transaction = tuple.getT3();

                            transaction.setCard(card);
                            transaction.setCustomer(customer);
                            return transaction;
                        })
                );
    }

    @Override
    public Flux<Transaction> getAll() {
        return (Flux<Transaction>) transactionRepository;
    }

    @Override
    public Mono<Transaction> getById(UUID transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    public Mono<Transaction> getByIdWithDetails(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .flatMap(transactionEntity ->
                        Mono.zip(
                                Mono.just(transactionEntity),
                                customerService.getById(transactionEntity.getCardNumber())
                        ).map(tuple -> {
                            Transaction transaction = tuple.getT1();
                            Customer customer = tuple.getT2();
                            transaction.setCard(Card.builder()
                                    .cardNumber(transaction.getCardNumber())
                                    .build());
                            transaction.setCustomer(customer);
                            return transaction;
                        })
                );
    }

    @Override
    public Mono<Transaction> update(Transaction transaction) {
        return transactionRepository.save(transaction.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


    @Transactional
    @Override
    public Mono<Transaction> processTopupTransaction(Transaction transaction, String merchantId) {
        if (transaction.getTransactionId() == null) {
            return Mono.error(new IllegalArgumentException("Transaction ID cannot be null"));
        }

        transaction.setCardNumber(transaction.getCard().getCardNumber());
        Customer customer = Customer.builder()
                .firstName(transaction.getCustomer().getFirstName())
                .lastName(transaction.getCustomer().getLastName())
                .country(transaction.getCustomer().getCountry())
                .cardNumber(transaction.getCardNumber())
                .build();


        log.warn("Saving transaction {}", transaction);
        log.warn("For merchant {}", merchantId);

        if (!transaction.getPaymentMethod().equals("CARD")) {
            return Mono.error(new RequestTopUpTransactionInvalidPaymentMethodException("Invalid payment method: " + transaction.getPaymentMethod()));
        } else {
            Mono<Card> saveCard = cardService.saveCardInTransaction(transaction.getCard());
            Mono<Customer> saveCustomerData = customerService.saveCustomerInTransaction(customer);
            Mono<Account> saveAccountData = saveAccountData(transaction, merchantId);

            log.warn("Saving topup transaction: {}", transaction);
            return Mono.zip(saveCard, saveCustomerData, saveAccountData)
                    .flatMap(tuple -> {
                        Card card = tuple.getT1();
                        Customer savedCustomer = tuple.getT2();
                        Account account = tuple.getT3();

                        transaction.setCardNumber(card.getCardNumber());
                        transaction.setCustomer(savedCustomer);
                        transaction.setAccountId(account.getId());
                        return transactionRepository.save(transaction);
                    })
                    .flatMap(savedTransaction -> {
                        try {
                            String dtoJson = objectMapper.writeValueAsString(WebhookDto.builder()
                                    .transactionId(transaction.getTransactionId())
                                    .paymentMethod(transaction.getPaymentMethod())
                                    .amount(transaction.getAmount())
                                    .currency(transaction.getCurrency())
                                    .type(transaction.getTransactionType().toString())
                                    .language(transaction.getLanguage())
                                    .cardData(cardMapper.mapToWebhookCardDataDto(transaction.getCard()))
                                    .customer(customerMapper.mapToWebhookCustomerDto(transaction.getCustomer()))
                                    .createdAt(transaction.getCreatedAt())
                                    .transactionStatus(transaction.getTransactionStatus())
                                    .message("OK")
                                    .build());

                            Webhook webhook = Webhook.builder()
                                    .transactionId(savedTransaction.getTransactionId())
                                    .urlRequest(transaction.getNotificationUrl())
                                    .transactionAttempt(0L)
                                    .bodyRequest(dtoJson)
                                    .createdAt(transaction.getCreatedAt())
                                    .createdBy("SYSTEM")
                                    .transactionStatus(transaction.getTransactionStatus())
                                    .build();

                            log.warn("Saving webhook {}", webhook);

                            return webhookNotificationService.saveWebhook(webhook)
                                    .flatMap(webhookNotificationService::sendWebhook)
                                    .thenReturn(savedTransaction
                                    );

                        } catch (JsonProcessingException e) {
                            return Mono.error(new RuntimeException("Error processing JSON", e));
                        }
                    })
                    .flatMap(savedTransaction -> {
                        savedTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
                        return update(savedTransaction);
                    })
                    .flatMap(updatedTransaction -> webhookRepository.findByTransactionId(updatedTransaction.getTransactionId())
                            .flatMap(webhook -> {
                                try {
                                    String dtoJson = objectMapper.writeValueAsString(WebhookDto.builder()
                                            .transactionId(updatedTransaction.getTransactionId())
                                            .paymentMethod(updatedTransaction.getPaymentMethod())
                                            .amount(updatedTransaction.getAmount())
                                            .currency(updatedTransaction.getCurrency())
                                            .type(updatedTransaction.getTransactionType().toString())
                                            .language(updatedTransaction.getLanguage())
                                            .cardData(cardMapper.mapToWebhookCardDataDto(updatedTransaction.getCard()))
                                            .customer(customerMapper.mapToWebhookCustomerDto(updatedTransaction.getCustomer()))
                                            .createdAt(updatedTransaction.getCreatedAt())
                                            .transactionStatus(updatedTransaction.getTransactionStatus())
                                            .message("OK")
                                            .build());
                                    webhook.setBodyRequest(dtoJson);
                                    webhook.setTransactionStatus(updatedTransaction.getTransactionStatus());
                                    webhook.setUpdatedAt(LocalDateTime.now());
                                    webhook.setUpdatedBy("SYSTEM");
                                    log.warn("Saving webhook {}", webhook);
                                    return webhookNotificationService.saveWebhook(webhook)
                                            .flatMap(webhookNotificationService::sendWebhook)
                                            .thenReturn(updatedTransaction);
                                } catch (JsonProcessingException e) {
                                    return Mono.error(new RuntimeException("Error processing JSON", e));
                                }
                            }))
                    .doOnSuccess(updatedTransaction -> log.warn("Transaction saved successfully: {}", updatedTransaction))
                    .doOnError(error -> log.warn("Error saving transaction: {}", error.getMessage()));
        }
    }

    private Mono<Account> saveAccountData(Transaction transaction, String merchantId) {
        return accountService.saveAccountInTransaction(
                Account.builder()
                        .merchantId(merchantId)
                        .currency(transaction.getCurrency())
                        .amount(transaction.getAmount())
                        .build());
    }

    @Override
    @Transactional
    public Mono<Transaction> processPayoutTransaction(Transaction transaction, String merchantId) {
        if (transaction.getTransactionId() == null) {
            return Mono.error(new IllegalArgumentException("Transaction ID cannot be null"));
        }

        transaction.setCardNumber(transaction.getCard().getCardNumber());
        Customer customer = Customer.builder()
                .firstName(transaction.getCustomer().getFirstName())
                .lastName(transaction.getCustomer().getLastName())
                .country(transaction.getCustomer().getCountry())
                .cardNumber(transaction.getCardNumber())
                .build();
        log.warn("Payout transaction: {}", transaction);
        log.warn("For merchant: {}", merchantId);

        if (!transaction.getPaymentMethod().equals("CARD")) {
            return Mono.error(new RequestTopUpTransactionInvalidPaymentMethodException("Invalid payment method: " + transaction.getPaymentMethod()));
        } else {
            Mono<Card> saveCard = cardService.saveCardInTransaction(transaction.getCard());
            Mono<Account> accountMono = accountService.getByMerchantIdAndCurrency(merchantId, transaction.getCurrency());
            Mono<Customer> saveCustomerData = customerService.saveCustomerInTransaction(customer);

            log.warn("Saving payout transaction: {}", transaction);
            return Mono.zip(saveCard, saveCustomerData, accountMono)
                    .flatMap(tuple -> {
                        Card card = tuple.getT1();
                        Customer savedCustomer = tuple.getT2();
                        Account account = tuple.getT3();

                        transaction.setCardNumber(card.getCardNumber());
                        transaction.setCustomer(savedCustomer);
                        transaction.setAccountId(account.getId());


                        return save(transaction.toBuilder()
                                .transactionType(TransactionType.PAYOUT)
                                .paymentMethod(transaction.getPaymentMethod())
                                .amount(transaction.getAmount())
                                .currency(transaction.getCurrency())
                                .language(transaction.getLanguage())
                                .notificationUrl(transaction.getNotificationUrl())
                                .createdBy("SYSTEM")
                                .transactionStatus(TransactionStatus.IN_PROGRESS)
                                .updatedAt(LocalDateTime.now())
                                .build())
                                .flatMap(savedTransaction -> {
                                    BigDecimal accountAmount = account.getAmount();
                                    if (accountAmount != null && accountAmount.compareTo(transaction.getAmount()) >= 0) {
                                        BigDecimal newAccountAmount = accountAmount.subtract(transaction.getAmount());
                                        return accountService.update(
                                                        account.toBuilder()
                                                                .amount(newAccountAmount)
                                                                .updatedAt(LocalDateTime.now())
                                                                .build())
                                                .then(transactionRepository.save(transaction.toBuilder()
                                                        .transactionType(TransactionType.PAYOUT)
                                                        .paymentMethod(transaction.getPaymentMethod())
                                                        .amount(transaction.getAmount())
                                                        .currency(transaction.getCurrency())
                                                        .language(transaction.getLanguage())
                                                        .notificationUrl(transaction.getNotificationUrl())
                                                        .createdBy("SYSTEM")
                                                        .transactionStatus(TransactionStatus.SUCCESS)
                                                        .updatedAt(LocalDateTime.now())
                                                        .build()));
                                    } else {
                                        return Mono.error(new RequestPayoutTransactionInvalidAmountException("PAYOUT_MIN_AMOUNT"));
                                    }
                                });
                    });
        }
    }
}