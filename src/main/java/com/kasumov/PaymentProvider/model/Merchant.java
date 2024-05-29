package com.kasumov.PaymentProvider.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("merchants")
public class Merchant implements Persistable<String> {

    @Id
    private String merchantId;
    private String secretKey;

    private Long wallet_id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private TransactionStatus transactionStatus;

    @Transient
    private List<Account> accountEntities;


    @Override
    public String getId() {
        return "";
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(merchantId);
    }
}
