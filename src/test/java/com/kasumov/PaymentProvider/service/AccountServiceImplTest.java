package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Account;
import com.kasumov.PaymentProvider.model.Currency;
import com.kasumov.PaymentProvider.repository.AccountRepository;
import com.kasumov.PaymentProvider.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    private AccountServiceImpl accountService;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void saveAccountInTransaction_NewAccount_Success() {
        Account newAccount = Account.builder()
                .merchantId("merchant123")
                .currency(Currency.USD)
                .amount(new BigDecimal("100.00"))
                .build();


        when(accountRepository.findByMerchantIdAndCurrency("merchant123", Currency.USD)).thenReturn(Mono.empty());
        when(accountRepository.save(newAccount)).thenReturn(Mono.just(newAccount));
        Mono<Account> savedAccountMono = accountService.saveAccountInTransaction(newAccount);
        Account savedAccount = savedAccountMono.block();
        assert savedAccount != null;
        assertEquals(new BigDecimal("100.00"), savedAccount.getAmount());
    }

    @Test
    void saveAccountInTransaction_ExistingAccount_Success() {

        Account existingAccount = Account.builder()
                .merchantId("merchant123")
                .currency(Currency.USD)
                .amount(new BigDecimal("200.00"))
                .build();


        when(accountRepository.findByMerchantIdAndCurrency("merchant123", Currency.USD)).thenReturn(Mono.just(existingAccount));
        Mono<Account> savedAccountMono = accountService.saveAccountInTransaction(existingAccount);
        Account savedAccount = savedAccountMono.block();
        assert savedAccount != null;
        assertEquals(new BigDecimal("300.00"), savedAccount.getAmount());
    }
}