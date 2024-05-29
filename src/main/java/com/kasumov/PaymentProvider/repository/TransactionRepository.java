package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.Transaction;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.model.TransactionType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {


    @Query("SELECT * FROM transactions WHERE transactions.account_id = :id AND transaction_type = :type AND created_at >= :startDate AND created_at <= :endDate")
    Flux<Transaction> findAllTransactionsForAccountByTypeAndPeriod(TransactionType type, LocalDateTime startDate, LocalDateTime endDate, Long id);

    @Query("SELECT * FROM transactions WHERE transactions.account_id = :id AND transaction_type = :type AND DATE (created_at) = :date")
    Flux<Transaction> findAllTransactionsForAccountByTypeAndDay(TransactionType type, LocalDate date, Long id);

    Mono<Void> deleteById(UUID id);

    Flux<Transaction> findTopUpTransactions(TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

    Flux<Transaction> findTransactionsByTypeAndDay(TransactionType type, LocalDate date);

    Flux<Transaction> findAllByStatus(TransactionStatus status);

    Flux<Transaction> findAllByCreatedAtAfterAndTransactionType(LocalDateTime createdAt, TransactionType transactionType);

    Flux<Transaction> findAllByCreatedAtAfterAndCreatedAtBeforeAndTransactionType(LocalDateTime startDate, LocalDateTime endDate, TransactionType transactionType);
}
