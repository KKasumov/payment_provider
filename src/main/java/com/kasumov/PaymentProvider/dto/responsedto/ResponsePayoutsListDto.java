package com.kasumov.PaymentProvider.dto.responsedto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class ResponsePayoutsListDto {
    private List<ResponseTransactionDetailsDto> payoutList;
}
