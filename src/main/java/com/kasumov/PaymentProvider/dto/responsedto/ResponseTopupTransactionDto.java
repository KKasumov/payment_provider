package com.kasumov.PaymentProvider.dto.responsedto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kasumov.PaymentProvider.model.Currency;
import com.kasumov.PaymentProvider.model.Language;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class ResponseTopupTransactionDto {
    private UUID transactionId;
    private String paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ResponseCardDto cardData;
    private Language language;
    private String notificationUrl;
    private ResponseCustomerDto customer;
}
