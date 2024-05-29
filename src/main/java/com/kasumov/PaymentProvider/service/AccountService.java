package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Account;
import com.kasumov.PaymentProvider.model.Currency;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService extends GenericService<Account,Long>{
    Mono<Account> getByMerchantIdAndCurrency(String merchantId, Currency currency);
    Mono<Account> saveAccountInTransaction(Account accountEntity);
    Flux<Account> getAllAccountsForMerchant(String merchantId);
}
