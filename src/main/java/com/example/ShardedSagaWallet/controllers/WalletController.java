package com.example.ShardedSagaWallet.controllers;

import com.example.ShardedSagaWallet.adapters.WalletAdapter;
import com.example.ShardedSagaWallet.dto.CreditWalletRequestDTO;
import com.example.ShardedSagaWallet.dto.DebitWalletRequestDTO;
import com.example.ShardedSagaWallet.dto.WalletRequestDTO;
import com.example.ShardedSagaWallet.dto.WalletResponseDTO;
import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.services.WalletService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody WalletRequestDTO walletRequestDTO)
    {
            try{
                WalletResponseDTO responseDTO = WalletAdapter.toDTO(walletService.createWallet(walletRequestDTO));
                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            }
            catch(Exception e)
            {
                log.error("Error while creating wallet", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<WalletResponseDTO> getWalletById(@PathVariable Long id) {
        WalletResponseDTO dto = WalletAdapter.toDTO(walletService.getByWalletId(id));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Long id) {
        BigDecimal balance = walletService.getWalletBalance(id);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<WalletResponseDTO> debitWallet(@PathVariable Long userId, @Valid @RequestBody DebitWalletRequestDTO debitWalletRequestDTO)
    {
       walletService.debit(userId,debitWalletRequestDTO);
       Wallet wallet=walletService.getWalletByUserId(userId);
       return ResponseEntity.ok(WalletAdapter.toDTO(wallet));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<WalletResponseDTO> debitWallet(@PathVariable Long userId, @Valid @RequestBody CreditWalletRequestDTO creditWalletRequestDTO)
    {
        walletService.credit(userId,creditWalletRequestDTO);
        Wallet wallet=walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(WalletAdapter.toDTO(wallet));
    }
}
