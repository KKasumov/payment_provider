package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.MerchantDTO;
import com.kasumov.PaymentProvider.model.Merchant;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MerchantMapper {
    MerchantDTO convert(Merchant merchant);
    Merchant convert(MerchantDTO dto);
}

