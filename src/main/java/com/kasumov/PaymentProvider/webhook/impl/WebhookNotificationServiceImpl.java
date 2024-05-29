package com.kasumov.PaymentProvider.webhook.impl;

import com.kasumov.PaymentProvider.model.Transaction;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.model.Webhook;
import com.kasumov.PaymentProvider.repository.WebhookRepository;
import com.kasumov.PaymentProvider.exception.InvalidWebhookDataException;
import com.kasumov.PaymentProvider.webhook.WebhookNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookNotificationServiceImpl implements WebhookNotificationService {
    private final WebhookRepository webhookRepository;
    private final WebClient webClient = WebClient.builder().build();

    @Override
    public Mono<Webhook> saveWebhook(Webhook webhook) {
        return validateWebhook(webhook)
                .flatMap(validatedWebhookEntity -> webhookRepository.save(webhook.toBuilder()
                        .transactionAttempt(0L)
                        .createdBy("SYSTEM")
                        .updatedBy("SYSTEM")
                        .transactionStatus(TransactionStatus.IN_PROGRESS)
                        .build()));
    }

    @Override
    public Mono<Webhook> updateWebhook(Webhook webhook) {
        return webhookRepository.save(webhook.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Webhook> sendWebhook(Webhook webhook) {
        return webClient.post()
                .uri(webhook.getUrlRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(webhook.getBodyRequest()), Transaction.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> webhook.toBuilder()
                        .bodyResponse(responseBody)
                        .statusResponse(HttpStatus.OK.toString())
                        .build())
                .flatMap(webhookRepository::save)
                .onErrorResume(error -> {
                    webhook.setStatusResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString());
                    webhook.setMessage(error.getMessage());
                    return webhookRepository.save(webhook);
                });
    }

    private Mono<Webhook> validateWebhook(Webhook webhook) {
        log.warn("Validating webhook {}", webhook);
        if (webhook.getTransactionId() == null) {
            return Mono.error(new InvalidWebhookDataException("Webhook ID cannot be null"));
        }

        if (webhook.getUrlRequest() == null) {
            return Mono.error(new InvalidWebhookDataException("URL request cannot be null"));
        }

        if (webhook.getBodyRequest() == null) {
            return Mono.error(new InvalidWebhookDataException("Body request cannot be null"));
        }
        return Mono.just(webhook);
    }
}
