package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.webhook.WebhookDto;
import com.kasumov.PaymentProvider.model.Webhook;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    WebhookDto mapToDto(Webhook webhook);

    @InheritInverseConfiguration
    Webhook mapFromDto(WebhookDto webhookDto);
}
