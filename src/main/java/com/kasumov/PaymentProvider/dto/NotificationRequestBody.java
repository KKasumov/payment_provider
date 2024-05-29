package com.kasumov.PaymentProvider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class NotificationRequestBody {

    @JsonProperty("transaction_id")
    private UUID transactionId;
    private String status;
    private String message;
}
