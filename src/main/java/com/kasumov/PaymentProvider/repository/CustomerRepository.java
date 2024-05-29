package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.Customer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, String> {
    @Modifying
    Mono<Void> deleteById(String cardNumber);
}
