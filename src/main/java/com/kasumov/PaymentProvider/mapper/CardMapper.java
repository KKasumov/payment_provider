package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.requestdto.RequestCardDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponseCardDto;
import com.kasumov.PaymentProvider.dto.webhook.WebhookResponseCardDataDto;
import com.kasumov.PaymentProvider.model.Card;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface CardMapper {

    ResponseCardDto mapToDto(Card card);


    @Named("maskCardData")
    default String maskCardData(String cardNumber) {
        return cardNumber.substring(0, 4) + "***" + cardNumber.substring(12);
    }

    @InheritInverseConfiguration
    Card mapFromDto(RequestCardDto requestCardDto);

    @Mapping(target = "cardNumber", qualifiedByName = "maskCardData")
    WebhookResponseCardDataDto mapToWebhookCardDataDto(Card cardEntity);

    }


