package com.example.ShardedSagaWallet.adapters;

import com.example.ShardedSagaWallet.dto.TransactionRequestDTO;
import com.example.ShardedSagaWallet.dto.TransactionResponseDTO;
import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.TransactionStatus;
import com.example.ShardedSagaWallet.entities.TransactionType;

import java.math.BigDecimal;

public class TransactionAdapter {

    public static Transaction toEntity(TransactionRequestDTO dto) {
        return Transaction.builder()
                .fromWalletId(dto.getFromWalletId())
                .toWalletId(dto.getToWalletId())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .build();
    }
    public static TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .fromWalletId(transaction.getFromWalletId())
                .toWalletId(transaction.getToWalletId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .sagaInstanceId(transaction.getSagaInstanceId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
