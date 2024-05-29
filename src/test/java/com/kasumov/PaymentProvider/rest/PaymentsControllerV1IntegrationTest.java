package com.kasumov.PaymentProvider.rest;

import com.kasumov.PaymentProvider.dto.responsedto.ResponseInProgressDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponsePayoutsListDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponseTransactionDetailsDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponseTransactionsListDto;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;



@SpringBootTest
@AutoConfigureWebTestClient
class PaymentsControllerV1IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testProcessTopupTransaction() {
        // Given
        String requestBody = """
                {
                  "amount": 10000,
                  "currency": "USD",
                  "source": {
                    "type": "CARD",
                    "cardNumber": "4242424242424242",
                    "expiryMonth": 12,
                    "expiryYear": 2024,
                    "cvv": "123"
                  }
                }
                """;

        // When
        webTestClient.post()
                .uri("/api/v1/payments/topups/")
                .bodyValue(requestBody)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(ResponseInProgressDto.class)
                .value(response -> {
                    Assertions.assertEquals(TransactionStatus.IN_PROGRESS, response.getStatus());
                    Assertions.assertNotNull(response.getTransactionId());
                });
    }

    @Test
    void testProcessPayoutTransaction() {
        // Given
        String requestBody = """
                {
                  "amount": 10000,
                  "currency": "USD",
                  "destination": {
                    "type": "CARD",
                    "cardNumber": "4242424242424242",
                    "expiryMonth": 12,
                    "expiryYear": 2024,
                    "cvv": "123"
                  }
                }
                """;

        // When
        webTestClient.post()
                .uri("/api/v1/payments/payout/")
                .bodyValue(requestBody)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(ResponseInProgressDto.class)
                .value(response -> {
                    Assertions.assertEquals(TransactionStatus.IN_PROGRESS, response.getStatus());
                    Assertions.assertNotNull(response.getTransactionId());
                });
    }

    @Test
    void testGetAllTransactionsList() {
        // Given
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";

        // When
        webTestClient.get()
                .uri("/api/v1/payments/transaction/list?start_date={startDate}&end_date={endDate}", startDate, endDate)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(ResponseTransactionsListDto.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getTransactionList());
                    Assertions.assertFalse(response.getTransactionList().isEmpty());
                });
    }

    @Test
    void testGetAllPayoutsList() {
        // Given
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";

        // When
        webTestClient.get()
                .uri("/api/v1/payments/payout/list?start_date={startDate}&end_date={endDate}", startDate, endDate)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(ResponsePayoutsListDto.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getPayoutList());
                    Assertions.assertFalse(response.getPayoutList().isEmpty());
                });
    }


    @Test
    void testGetTransactionDetails() {
        // Given
        UUID transactionId = UUID.randomUUID();

        // When
    webTestClient.get()
            .uri("/api/v1/payments/transaction/{transactionId}/details", transactionId)
            .exchange()

            // Then
            .expectStatus().isOk()
            .expectBody(ResponseTransactionDetailsDto.class)
            .value(response -> {
                Assertions.assertEquals(transactionId, response.getTransactionId());
                Assertions.assertEquals(TransactionStatus.IN_PROGRESS, response.getTransactionStatus());
            });
}
}