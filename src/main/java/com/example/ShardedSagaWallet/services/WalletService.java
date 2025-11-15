package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;


    public Wallet createWallet(Long userId) {
        Wallet wallet= Wallet.builder()
                .userId(userId)
                .isactive(true)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = walletRepository.save(wallet);
        log.info("Wallet created with id {}", wallet.getId());
        return wallet;
    }

    public Wallet getByWalletId(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Transactional
    public void debit(Long walletId , BigDecimal amount){
        log.info("Debiting {} from wallet {}" , amount , walletId);
        Wallet wallet =  getByWalletId(walletId);
        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("Debit successful for wallet {}", walletId);
    }

    @Transactional
    public void credit(Long walletId , BigDecimal amount){
        log.info("Crediting {} from wallet {}" , amount , walletId);
        Wallet wallet =  getByWalletId(walletId);
        wallet.credit(amount);
        walletRepository.save(wallet);
        log.info("Credit successful for wallet {}", walletId);
    }

    public BigDecimal getWalletBalance(Long walletId){
        log.info("Getting wallet balance for wallet {}", walletId);
        BigDecimal balance= getByWalletId(walletId).getBalance();
        log.info("Wallet balance successful for wallet {} is {}", walletId,balance);
        return balance;
    }
}
