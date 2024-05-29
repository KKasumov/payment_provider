package com.kasumov.PaymentProvider.service;

import com.kasumov.PaymentProvider.model.Customer;
import com.kasumov.PaymentProvider.repository.CustomerRepository;
import com.kasumov.PaymentProvider.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerServiceImplTest {

    private CustomerServiceImpl customerService;
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void saveCustomerInTransaction_NewCustomer_Success() {

        Customer newCustomer = Customer.builder()
                .cardNumber("123")
                .firstName("Anna")
                .country("USA")
                .build();


        when(customerRepository.findById("customer123")).thenReturn(Mono.empty());
        when(customerRepository.save(newCustomer)).thenReturn(Mono.just(newCustomer));
        Mono<Customer> savedCustomerMono = customerService.saveCustomerInTransaction(newCustomer);
        Customer savedCustomer = savedCustomerMono.block();

        assert savedCustomer != null;
        assertEquals("USA", savedCustomer.getCountry());
    }

    @Test
    void saveCustomerInTransaction_ExistingCustomer_Success() {

        Customer existingCustomer = Customer.builder()
                .cardNumber("123")
                .firstName("Anna")
                .country("USA")
                .build();

        when(customerRepository.findById("customer123")).thenReturn(Mono.just(existingCustomer));
        Mono<Customer> savedCustomerMono = customerService.saveCustomerInTransaction(existingCustomer);
        Customer savedCustomer = savedCustomerMono.block();

        assert savedCustomer != null;
        assertEquals("Bob", savedCustomer.getFirstName());
    }
}