package com.kasumov.PaymentProvider.dto.requestdto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kasumov.PaymentProvider.model.Currency;
import com.kasumov.PaymentProvider.model.Language;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class RequestTopupTransactionDto {
    private String paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private RequestCardDto card;
    private Language language;
    private String notificationUrl;
    private RequestCustomerDto customer;
}
