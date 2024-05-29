package com.kasumov.PaymentProvider.exception;

public class PayoutTransactionInvalidAmountException extends  RuntimeException{

    public PayoutTransactionInvalidAmountException(String message){
        super(message);
    }
}
