package com.example.ShardedSagaWallet.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebitWalletRequestDTO {

    @NotNull(message="Amount is required")
    private BigDecimal amount;
}
