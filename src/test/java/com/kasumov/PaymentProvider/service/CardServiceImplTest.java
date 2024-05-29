package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Card;
import com.kasumov.PaymentProvider.repository.CardRepository;
import com.kasumov.PaymentProvider.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardServiceImplTest {

    private CardServiceImpl cardService;
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        cardService = new CardServiceImpl(cardRepository);
    }

    @Test
    void saveCardInTransaction_NewCard_Success() {
        Card newCard = new Card();
        when(cardRepository.findById("1234567890")).thenReturn(Mono.empty());
        when(cardService.saveNewCard(any(Card.class))).thenReturn(Mono.just(newCard));
        Mono<Card> savedCardMono = cardService.saveCardInTransaction(newCard);
        Card savedCard = savedCardMono.block();
        assert savedCard != null;
        assertEquals("1234567890", savedCard.getCardNumber());
    }

    @Test
    void saveCardInTransaction_ExistingCard_Failure() {
        Card existingCard = new Card();
        when(cardRepository.findById("0987654321")).thenReturn(Mono.just(existingCard));
        Mono<Card> savedCardMono = cardService.saveCardInTransaction(existingCard);
        Card savedCard = savedCardMono.block();
        assertNull(savedCard);
    }
}