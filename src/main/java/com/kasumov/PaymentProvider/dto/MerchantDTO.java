package com.kasumov.PaymentProvider.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantDTO {

    private Long id;
    private String name;
    private String secret;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long wallet_id;
}
