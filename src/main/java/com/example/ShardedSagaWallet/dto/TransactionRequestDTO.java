package com.example.ShardedSagaWallet.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message="fromWalletId is required")
    private Long fromWalletId;

    @NotNull(message="toWalletId is required")
    private Long toWalletId;

    @NotNull(message="amount is required")
    private BigDecimal amount;

    @NotNull(message="description is required")
    private String description;

}
