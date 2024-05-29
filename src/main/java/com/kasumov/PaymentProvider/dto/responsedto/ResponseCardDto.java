package com.kasumov.PaymentProvider.dto.responsedto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class ResponseCardDto {
    private String cardNumber;



    public void maskCardNumber(int start, int end) {
        if (start >= 0 && end < cardNumber.length()) {
            StringBuilder maskedCardNumber = new StringBuilder(cardNumber);
            for (int i = start; i < end; i++) {
                maskedCardNumber.setCharAt(i, '*');
            }
        } else {
            throw new IllegalArgumentException("Invalid start and end indices for masking card number.");
        }
    }
}
