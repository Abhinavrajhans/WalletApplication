package com.example.ShardedSagaWallet.dto;

import com.example.ShardedSagaWallet.entities.TransactionStatus;
import com.example.ShardedSagaWallet.entities.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseDTO {

    private Long id;
    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;
    private TransactionStatus status;
    private TransactionType type;
    private String description;
    private Long sagaInstanceId;
    private Date createdAt;
}
