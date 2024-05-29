package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.requestdto.RequestCustomerDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponseCustomerDto;
import com.kasumov.PaymentProvider.dto.webhook.WebhookResponseCustomerDto;
import com.kasumov.PaymentProvider.model.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    ResponseCustomerDto mapToDto(Customer customer);

    @InheritInverseConfiguration
    Customer mapFromDto(RequestCustomerDto requestCustomerDto);

    WebhookResponseCustomerDto mapToWebhookCustomerDto(Customer customer);
}