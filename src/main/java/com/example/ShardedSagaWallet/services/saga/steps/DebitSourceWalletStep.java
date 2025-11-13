package com.example.ShardedSagaWallet.services.saga.steps;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebitSourceWalletStep implements SagaStep {

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext sagaContext) {
        // step 1: from the saga context we have to get the source wallet id
        Long fromWalletId = sagaContext.getLong("fromWalletId");
        // the wallet from which we have to do the money transfer
        BigDecimal amount = sagaContext.getBigDecimal("amount");
        // this is the amount which we want to deduct from the current wallet
        log.info("Debiting source wallet {} with amount {}", fromWalletId, amount);
        // we will try to find the wallet
        Wallet wallet = walletRepository.findById(fromWalletId).
                orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        log.info("Wallet fetched with balance {}", wallet.getBalance());
        sagaContext.put("originalSourceWalletBalance", wallet.getBalance());
        //in the wallet we had a method called as hasSufficientBalance
        //if we need to deduct some money then we have to check whether the wallet has a sufficient money or not
        // but we are doing this check in the wallet.debit function so no need to write anything extra
        //if everthing is in place , same thing as credit
        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("wallet saved with balance {}", wallet.getBalance());
        sagaContext.put("sourceWalletBalanceAfterDebit",wallet.getBalance());
        log.info("Debiting source wallet step executed successfully");
        return true;
    }

    @Override
    public boolean compensate(SagaContext sagaContext) {
        // step 1: from the saga context we have to get the source wallet id
        Long fromWalletId = sagaContext.getLong("fromWalletId");
        // the wallet from which we have to do the money transfer
        BigDecimal amount = sagaContext.getBigDecimal("amount");
        // this is the amount which we want to deduct from the current wallet
        log.info("Compensating source wallet {} with amount {}", fromWalletId, amount);
        // we will try to find the wallet
        Wallet wallet = walletRepository.findById(fromWalletId).
                orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        log.info("Wallet fetched with balance {}", wallet.getBalance());
        sagaContext.put("sourceWalletBalanceBeforeCreditCompensation", wallet.getBalance());

        wallet.credit(amount);
        walletRepository.save(wallet);

        log.info("wallet saved with balance {}", wallet.getBalance());
        sagaContext.put("sourceWalletBalanceAfterCreditCompensation",wallet.getBalance());
        log.info("Compensating source wallet step executed successfully");
        return true;
    }

    @Override
    public String getStepName() {
        return "DebitSourceWalletStep";
    }
}
