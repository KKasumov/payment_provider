package com.kasumov.PaymentProvider.exception;

public class RequestTopUpTransactionInvalidPaymentMethodException extends RuntimeException{

    public RequestTopUpTransactionInvalidPaymentMethodException(String message) {
        super(message);
    }
}
