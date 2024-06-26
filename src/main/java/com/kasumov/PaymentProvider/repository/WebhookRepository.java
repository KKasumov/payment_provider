package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.Webhook;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface WebhookRepository extends R2dbcRepository<Webhook, Long> {

    @Query("SELECT * FROM webhooks WHERE transaction_id = $1")
    Mono<Webhook> findByTransactionId(UUID transactionId);
}
