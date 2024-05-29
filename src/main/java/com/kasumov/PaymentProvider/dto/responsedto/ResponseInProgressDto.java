package com.kasumov.PaymentProvider.dto.responsedto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ResponseInProgressDto {
    private UUID transactionId;
    private TransactionStatus status;
    private String message;

}