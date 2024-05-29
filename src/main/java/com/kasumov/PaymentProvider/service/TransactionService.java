package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Transaction;
import com.kasumov.PaymentProvider.model.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public interface TransactionService extends GenericService<Transaction, UUID>{

    Flux<Transaction> getAllTransactionsForMerchantByTypeAndPeriod(TransactionType type, LocalDateTime startDate, LocalDateTime endDate, String merchantId);

    Flux<Transaction> getAllTransactionsForMerchantByTypeAndDay(TransactionType type, LocalDate date, String merchantId);

    Mono<Transaction> getByIdWithDetails(UUID transactionId);

    Mono<Transaction> processPayoutTransaction(Transaction transaction, String merchantId);

    Mono<Transaction> processTopupTransaction(Transaction transaction, String merchantId);
}