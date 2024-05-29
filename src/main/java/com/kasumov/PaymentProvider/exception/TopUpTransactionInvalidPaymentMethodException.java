package com.kasumov.PaymentProvider.exception;

public class TopUpTransactionInvalidPaymentMethodException extends RuntimeException{

    public TopUpTransactionInvalidPaymentMethodException(String message) {
        super(message);
    }
}
