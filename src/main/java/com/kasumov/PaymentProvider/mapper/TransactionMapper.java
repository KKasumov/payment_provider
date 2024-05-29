package com.kasumov.PaymentProvider.mapper;

import com.kasumov.PaymentProvider.dto.requestdto.RequestPayoutTransactionDto;
import com.kasumov.PaymentProvider.dto.requestdto.RequestTopupTransactionDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponsePayoutDetailsDto;
import com.kasumov.PaymentProvider.dto.responsedto.ResponseTransactionDetailsDto;
import com.kasumov.PaymentProvider.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, CardMapper.class})
public interface TransactionMapper {
    ResponseTransactionDetailsDto mapToResponseTransactionWithDetailsDto(Transaction transaction);
    ResponsePayoutDetailsDto mapToResponsePayoutWithDetailsDto(Transaction transaction);

    Transaction mapFromRequestTopupDto(RequestTopupTransactionDto requestTopupTransactionDto);

    Transaction mapFromRequestPayoutDto(RequestPayoutTransactionDto requestPayoutTransactionDto);

}
