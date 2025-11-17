package com.example.ShardedSagaWallet.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotBlank(message="fromWalletId is required")
    private Long fromWalletId;

    @NotBlank(message="toWalletId is required")
    private Long toWalletId;

    @NotBlank(message="amount is required")
    private BigDecimal amount;

    @NotBlank(message="description is required")
    private String description;

}
