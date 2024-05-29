package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService extends GenericService<Customer,String>{
    Mono<Customer> saveCustomerInTransaction(Customer customer);
}
