package com.kasumov.PaymentProvider.exception;

public class InvalidWebhookDataException extends RuntimeException {
    public InvalidWebhookDataException(String message) {
        super(message);
    }
}
