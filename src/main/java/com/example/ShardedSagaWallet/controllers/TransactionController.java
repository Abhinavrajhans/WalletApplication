package com.example.ShardedSagaWallet.controllers;


import com.example.ShardedSagaWallet.dto.TransactionRequestDTO;
import com.example.ShardedSagaWallet.dto.TransactionResponseDTO;
import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.services.TransactionService;
import com.example.ShardedSagaWallet.services.TransferSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final TransferSagaService transferSagaService;

    @PostMapping
    public ResponseEntity<Long> createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) {

        try{
            Long sagaInstanceId = transferSagaService.initiateTransfer(
                    transactionRequestDTO.getFromWalletId(),
                    transactionRequestDTO.getFromWalletId(),
                    transactionRequestDTO.getAmount(),
                    transactionRequestDTO.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(sagaInstanceId);
        }
        catch(Exception e)
        {
            log.error("Error creating transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
