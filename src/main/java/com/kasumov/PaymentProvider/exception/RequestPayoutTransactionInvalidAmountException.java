package com.kasumov.PaymentProvider.exception;

public class RequestPayoutTransactionInvalidAmountException extends  RuntimeException{

    public RequestPayoutTransactionInvalidAmountException(String message){
        super(message);
    }
}
