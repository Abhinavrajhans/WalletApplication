package com.example.ShardedSagaWallet.adapters;

import com.example.ShardedSagaWallet.dto.WalletRequestDTO;
import com.example.ShardedSagaWallet.dto.WalletResponseDTO;
import com.example.ShardedSagaWallet.entities.Wallet;

import java.math.BigDecimal;

public class WalletAdapter {

    public static Wallet toEntity(WalletRequestDTO dto) {
        return Wallet.builder()
                .userId(dto.getUserId())
                .isactive(true)
                .balance(BigDecimal.ZERO)
                .build();
    }

    public static WalletResponseDTO toDTO(Wallet entity) {
        return WalletResponseDTO.builder()
                .id(entity.getId())
                .isactive(entity.getIsactive())
                .balance(entity.getBalance())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
