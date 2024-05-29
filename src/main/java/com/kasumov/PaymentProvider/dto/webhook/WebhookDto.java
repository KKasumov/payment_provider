package com.kasumov.PaymentProvider.dto.webhook;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kasumov.PaymentProvider.model.Currency;
import com.kasumov.PaymentProvider.model.Language;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class WebhookDto {
    private UUID transactionId;
    private String paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private String  type;
    private WebhookResponseCardDataDto cardData;
    private WebhookResponseCustomerDto customer;
    private Language language;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TransactionStatus transactionStatus;
    private String message;
}
