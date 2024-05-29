package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.AccountDto;
import com.kasumov.PaymentProvider.model.Account;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto mapToDto(Account account);

    @InheritInverseConfiguration
    Account mapFromDto(AccountDto accountDto);

}
