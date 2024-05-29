package com.kasumov.PaymentProvider.dto;

import lombok.Data;

@Data
public class MerchantWalletDTO {

    private Long id;
    private Double amount;
    private String currency;
}
