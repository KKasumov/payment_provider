package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.Account;
import com.kasumov.PaymentProvider.model.Currency;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {

    Mono<Account> findByMerchantIdAndCurrency(String merchantId, Currency currency);
    Flux<Account> findAllByMerchantId(String merchantId);
}



