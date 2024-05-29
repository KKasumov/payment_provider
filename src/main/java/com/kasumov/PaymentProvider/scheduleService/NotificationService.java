package com.kasumov.PaymentProvider.scheduleService;

import com.kasumov.PaymentProvider.dto.NotificationRequestBody;
import com.kasumov.PaymentProvider.model.Transaction;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebhookRepository webhookRepository;

    public Mono<Void> sendTransactionNotification(Transaction transaction, String message, TransactionStatus status) {
        return webhookRepository.findById(transaction.getWebhookId())
                .flatMap(webhook -> {
                    WebClient client = WebClient.create();

                    return client.post()
                            .uri(URI.create(webhook.getNotificationUrl()))
                            .body(BodyInserters.fromValue(NotificationRequestBody.builder()
                                    .transactionId(transaction.getId())
                                    .message(message)
                                    .status(status.toString())
                                    .build()))
                            .retrieve()
                            .toEntity(String.class)
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(stringResponseEntity -> {
                                webhook.setResponseCode(stringResponseEntity.getStatusCode().value());
                                webhook.setTransactionAttempt(webhook.getTransactionAttempt() +1);
                                webhook.setUpdatedAt(LocalDateTime.now());
                                return webhookRepository.save(webhook).then();
                            })
                            .doFinally(signalType -> {
                                // Здесь можно добавить код, который должен выполниться после завершения всей цепочки
                                // Например, логирование или освобождение ресурсов
                            });
                });
    }

}
