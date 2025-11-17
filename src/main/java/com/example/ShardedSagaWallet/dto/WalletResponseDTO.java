package com.example.ShardedSagaWallet.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletResponseDTO {

    private Long id;
    private Long userId;
    private Boolean isactive;
    private BigDecimal balance;
}
