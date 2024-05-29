package com.kasumov.PaymentProvider.service.impl;

import com.kasumov.PaymentProvider.model.Card;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.repository.CardRepository;
import com.kasumov.PaymentProvider.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Flux<Card> getAll() {
        return cardRepository.findAll();
    }

    @Override
    public Mono<Card> getById(String cardNumber) {
        return cardRepository.findById(cardNumber);
    }

    @Override
    public Mono<Card> update(Card card) {
        return cardRepository.save(card.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Card> save(Card card) {
        return cardRepository.save(card);
    }



    public Mono<Card> saveCardInTransaction(Card card) {
        return cardRepository.findById(card.getCardNumber())
                .switchIfEmpty(saveNewCard(card).then(Mono.just(card)));
    }

    public Mono<Card> saveNewCard(Card card) {
        return Mono.defer(() -> {
            Card newCard = card.toBuilder()
                    .createdBy("SYSTEM")
                    .updatedBy("SYSTEM")
                    .transactionStatus(TransactionStatus.ACTIVE)
                    .build();
            return cardRepository.save(newCard);
        });
    }
}