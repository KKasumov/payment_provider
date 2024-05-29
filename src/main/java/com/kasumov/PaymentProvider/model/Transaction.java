package com.kasumov.PaymentProvider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction implements Persistable<UUID> {
    @Id
    private UUID transactionId;
    private String paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private Language language;
    private String notificationUrl;
    private String cardNumber;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private Long accountId;
    private Long webhookId;
    private Long cardId;
    private Long merchantId;
    //private Double amount;


    @Transient
    private Card card;

    @Transient
    private Customer customer;
    //private Long clientId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;



    @Override
    public UUID getId() {
        return transactionId;
    }



    @Override
    public boolean isNew() {
        return !getTransactionStatus().equals(TransactionStatus.SUCCESS)
                && !getTransactionStatus().equals(TransactionStatus.FAILED);
    }

}


