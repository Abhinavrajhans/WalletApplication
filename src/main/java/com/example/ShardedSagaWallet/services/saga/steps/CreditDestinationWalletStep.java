package com.example.ShardedSagaWallet.services.saga.steps;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStepInterface {

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext sagaContext) {
        // step 1: get the destination wallet id from the context
        Long toWalletId = sagaContext.getLong("toWalletId");
        // what is the amount u want to transfer
        BigDecimal amount = sagaContext.getBigDecimal("amount");
        log.info("Crediting destination wallet {} with amount {}", toWalletId, amount);
        // step 1 done
        // step 2: Fetch the destination wallet from the database with a lock
        // why we are doing it with a lock because we don't want to put ourself in any kind of a race condition.
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        log.info("Wallet fetched with balance {}", wallet.getBalance());
        // before the transaction if we want to store the balance of the wallet then we can store the balance in the context
        sagaContext.put("originalToWalletBalance", wallet.getBalance());
        // store the balance before the cerdit comes.
        // step 3 : Credit the destination wallet
        wallet.credit(amount);
        // by only updating the object only the java object in our memory will be updated.
        // in order to make sure this persists in the db u need to save using the repo
        walletRepository.save(wallet);
        log.info("Wallet saved with balance {}", wallet.getBalance());
        sagaContext.put("toWalletBalanceAfterCredit", wallet.getBalance());
        log.info("Credit Destination wallet step executed successfully");
        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext sagaContext) {
        // step 1: get the destination wallet id from the context
        Long toWalletId = sagaContext.getLong("toWalletId");
        // what is the amount u want to transfer
        BigDecimal amount = sagaContext.getBigDecimal("amount");
        log.info("Compensating credit of destination wallet {} with amount {}", toWalletId, amount);
        // step 1 done
        // step 2: Fetch the destination wallet from the database with a lock
        // why we are doing it with a lock because we don't want to put ourself in any kind of a race condition.
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        log.info("Wallet fetched with balance {}", wallet.getBalance());
        // before the transaction if we want to store the balance of the wallet then we can store the balance in the context
        sagaContext.put("originalToWalletBalance", wallet.getBalance());
        // store the balance before the cerdit comes.
        // step 3 : Debit the destination wallet
        wallet.debit(amount);
        // by only updating the object only the java object in our memory will be updated.
        // in order to make sure this persists in the db u need to save using the repo
        walletRepository.save(wallet);
        log.info("Wallet saved with balance {}", wallet.getBalance());
        sagaContext.put("toWalletBalanceAfterCreditCompensation", wallet.getBalance());
        log.info("Credit Compensation of Destination wallet step executed successfully");
        return true;

    }

    @Override
    public String getStepName() {
        return SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
    }

}
