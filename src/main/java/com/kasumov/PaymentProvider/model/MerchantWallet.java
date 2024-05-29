package com.kasumov.PaymentProvider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data

public class MerchantWallet {

    @Id
    private Long id;
    private BigDecimal amount;
    private String currency;
}
