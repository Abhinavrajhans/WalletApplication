package com.example.ShardedSagaWallet.entities;

public enum StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    COMPENSATING,
    COMPENSATED,
    SKIPPED
}
