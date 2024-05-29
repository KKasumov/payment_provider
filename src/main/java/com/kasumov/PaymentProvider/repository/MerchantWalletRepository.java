package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.MerchantWallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface MerchantWalletRepository extends R2dbcRepository<MerchantWallet, Long> {
}
