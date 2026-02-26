package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.adapters.WalletAdapter;
import com.example.ShardedSagaWallet.dto.CreditWalletRequestDTO;
import com.example.ShardedSagaWallet.dto.DebitWalletRequestDTO;
import com.example.ShardedSagaWallet.dto.WalletRequestDTO;
import com.example.ShardedSagaWallet.dto.WalletResponseDTO;
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


    public Wallet createWallet(WalletRequestDTO dto) {
        Wallet wallet= WalletAdapter.toEntity(dto);
        wallet = walletRepository.save(wallet);
        log.info("Wallet created with id {}", wallet.getId());
        return wallet;
    }

    public Wallet getByWalletId(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Wallet getWalletByUserId(Long userId) {
        log.info("Getting wallet by user id {}", userId);
        return walletRepository.findByUserId(userId).getFirst();
    }

    @Transactional
    public void debit(Long userId , DebitWalletRequestDTO debitWalletRequestDTO) {
        log.info("Debiting {} from userId {}" , debitWalletRequestDTO.getAmount() , userId);
        Wallet wallet =  getWalletByUserId(userId);
        walletRepository.updateBalanceByUserId(userId, wallet.getBalance().subtract(debitWalletRequestDTO.getAmount()));
        log.info("Debit successful for wallet {}", userId);
    }

    @Transactional
    public void credit(Long userId , CreditWalletRequestDTO creditWalletRequestDTO) {
        log.info("Crediting {} from userId {}" , creditWalletRequestDTO.getAmount() , userId);
        Wallet wallet =  getWalletByUserId(userId);
        walletRepository.updateBalanceByUserId(userId, wallet.getBalance().add(creditWalletRequestDTO.getAmount()));
        log.info("Credit successful for userId {}", userId);
    }

    public BigDecimal getWalletBalance(Long walletId){
        log.info("Getting wallet balance for wallet {}", walletId);
        BigDecimal balance= getByWalletId(walletId).getBalance();
        log.info("Wallet balance successful for wallet {} is {}", walletId,balance);
        return balance;
    }
}
