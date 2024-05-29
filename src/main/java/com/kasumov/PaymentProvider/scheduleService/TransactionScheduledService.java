package com.kasumov.PaymentProvider.scheduleService;

import com.kasumov.PaymentProvider.model.*;
import com.kasumov.PaymentProvider.repository.CardRepository;
import com.kasumov.PaymentProvider.repository.MerchantRepository;
import com.kasumov.PaymentProvider.repository.MerchantWalletRepository;
import com.kasumov.PaymentProvider.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionScheduledService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final MerchantWalletRepository merchantWalletRepository;
    private final MerchantRepository merchantRepository;
    private final NotificationService notificationService;

    public TransactionScheduledService(TransactionRepository transactionRepository, CardRepository cardRepository, MerchantWalletRepository merchantWalletRepository, MerchantRepository merchantRepository, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.merchantWalletRepository = merchantWalletRepository;
        this.merchantRepository = merchantRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 10 * 1000)
    public void doPayment() {
        transactionRepository.findAllByStatus(TransactionStatus.IN_PROGRESS)
                .flatMap(this::processTransaction)
                .then();
    }



    private Mono<Void> processTransaction(Transaction transaction) {
        return cardRepository.findById(String.valueOf(transaction.getCardId()))
                .flatMap(card -> merchantRepository.findById(String.valueOf(transaction.getMerchantId()))
                        .flatMap(merchant -> merchantWalletRepository.findById(merchant.getWallet_id())
                                .flatMap(merchantWallet -> processTransactionType(transaction, card, merchantWallet))));
    }

    private Mono<Void> processTransactionType(Transaction transaction, Card card, MerchantWallet merchantWallet) {
        if (transaction.getTransactionType().equals(TransactionType.TOPUP)) {
            return processTopupTransaction(transaction, card, merchantWallet);
        } else {
            return processOtherTransaction(transaction, card, merchantWallet);
        }
    }

    private Mono<Void> processTopupTransaction(Transaction transaction, Card card, MerchantWallet merchantWallet) {
        if (card.getAmount().compareTo(transaction.getAmount()) < 0) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return notificationService.sendTransactionNotification(transaction, "NOT_ENOUGH_MONEY", TransactionStatus.FAILED);
        } else if ((int) (Math.random() * 5) == 1) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return notificationService.sendTransactionNotification(transaction, "INTERNAL_ERROR", TransactionStatus.FAILED);
        } else {
            return processSuccessfulTransaction(transaction, card, merchantWallet);
        }
    }

    private Mono<Void> processOtherTransaction(Transaction transaction, Card card, MerchantWallet merchantWallet) {
        if (card.getAmount().compareTo(transaction.getAmount()) < 0) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return notificationService.sendTransactionNotification(transaction, "NOT_ENOUGH_MONEY", TransactionStatus.FAILED);
        } else if ((int) (Math.random() * 5) == 1) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return notificationService.sendTransactionNotification(transaction, "INTERNAL_ERROR", TransactionStatus.FAILED);
        } else {
            return processSuccessfulTransaction(transaction, card, merchantWallet);
        }
    }

    private Mono<Void> processSuccessfulTransaction(Transaction transaction, Card card, MerchantWallet merchantWallet) {
        card.setAmount(card.getAmount().add(transaction.getTransactionType() == TransactionType.TOPUP ? transaction.getAmount().negate() : transaction.getAmount()));
        merchantWallet.setAmount(merchantWallet.getAmount().add(transaction.getAmount()));
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        return notificationService.sendTransactionNotification(transaction, "OK", TransactionStatus.SUCCESS);
    }
}