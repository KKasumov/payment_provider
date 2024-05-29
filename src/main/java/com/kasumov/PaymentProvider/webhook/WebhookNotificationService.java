package com.kasumov.PaymentProvider.webhook;

import com.kasumov.PaymentProvider.model.Webhook;
import reactor.core.publisher.Mono;

public interface WebhookNotificationService {
    Mono<Webhook> saveWebhook(Webhook webhook);
    Mono<Webhook> updateWebhook(Webhook webhook);
    Mono<Webhook> sendWebhook(Webhook webhook);
}
