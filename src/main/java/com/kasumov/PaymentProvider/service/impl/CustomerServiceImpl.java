package com.kasumov.PaymentProvider.service.impl;

import com.kasumov.PaymentProvider.model.Customer;
import com.kasumov.PaymentProvider.model.TransactionStatus;
import com.kasumov.PaymentProvider.repository.CustomerRepository;
import com.kasumov.PaymentProvider.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Flux<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> getById(String customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Mono<Customer> update(Customer customer) {
        return customerRepository.save(customer.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Customer> saveCustomerInTransaction(Customer customer) {
        return customerRepository.findById(Objects.requireNonNull(customer.getId()))
                .switchIfEmpty(saveNewCustomer(customer));
    }

    /*@Override
    public Mono<Customer> deleteById(String customerId) {
        return customerRepository.findById(customerId)
                .flatMap(customer -> customerRepository.deleteById(customerId)
                        .thenReturn(customer));
    }*/

    private Mono<Customer> saveNewCustomer(Customer customer) {
        return Mono.defer(() -> {
            Customer newCustomer = customer.toBuilder()
                    .createdBy("SYSTEM")
                    .updatedBy("SYSTEM")
                    .transactionStatus(TransactionStatus.ACTIVE)
                    .build();
            return customerRepository.save(newCustomer);
        });
    }
}