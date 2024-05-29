package com.kasumov.PaymentProvider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class Account implements Persistable<Long> {
    @Id
    private Long id;
    private String merchantId;
    private Currency currency;
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private TransactionStatus transactionStatus;


    @Override
    public boolean isNew() {
        return this.id== null;
    }
}
