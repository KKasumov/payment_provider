package com.kasumov.PaymentProvider.repository;

import com.kasumov.PaymentProvider.model.Card;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends R2dbcRepository<Card, String> {
    @Modifying
    @Query("UPDATE cards SET status = 'DELETED' WHERE card_number = :cardNumber")
    Mono<Void> deleteById(String cardNumber);
}
