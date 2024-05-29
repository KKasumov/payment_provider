package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Card;
import reactor.core.publisher.Mono;

public interface CardService extends GenericService<Card,String>{

    Mono<Card> saveCardInTransaction(Card card);
}
