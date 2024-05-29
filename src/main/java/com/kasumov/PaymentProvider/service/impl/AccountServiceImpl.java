package com.kasumov.PaymentProvider.service.impl;

import com.kasumov.PaymentProvider.model.Account;
import com.kasumov.PaymentProvider.model.Currency;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.repository.AccountRepository;
import com.kasumov.PaymentProvider.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;


    @Override
    public Flux<Account> getAll() {
         return (Flux<Account>) accountRepository;
    }

    @Override
    public Mono<Account> getById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public Mono<Account> update(Account account) {
        return accountRepository.save(account.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Account> save(Account account) {
        return accountRepository.save(account);
    }

    public Mono<Account> saveAccountInTransaction(Account account) {
        return accountRepository.findByMerchantIdAndCurrency(account.getMerchantId(), account.getCurrency())
                .flatMap(existingAccount -> {
                    BigDecimal newAmount = existingAccount.getAmount().add(account.getAmount());
                    existingAccount.setAmount(newAmount);
                    existingAccount.setUpdatedAt(LocalDateTime.now());
                    return accountRepository.save(existingAccount);
                })
                .switchIfEmpty(Mono.defer(() -> accountRepository.save(
                        account.toBuilder()
                                .merchantId(account.getMerchantId())
                                .currency(account.getCurrency())
                                .amount(account.getAmount())
                                .createdBy("SYSTEM")
                                .updatedBy("SYSTEM")
                                .transactionStatus(TransactionStatus.ACTIVE)
                                .build())));
    }


    @Override
    public Mono<Account> getByMerchantIdAndCurrency(String merchantId, Currency currency) {
        return accountRepository.findByMerchantIdAndCurrency(merchantId, currency);
    }

    @Override
    public Flux<Account> getAllAccountsForMerchant(String merchantId) {
        return accountRepository.findAllByMerchantId(merchantId);
    }
}
