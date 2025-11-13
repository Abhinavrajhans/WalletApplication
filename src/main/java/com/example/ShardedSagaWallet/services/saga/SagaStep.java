package com.example.ShardedSagaWallet.services.saga;

public interface SagaStep {

    boolean execute(SagaContext sagaContext);

    boolean compensate(SagaContext sagaContext);

    String getStepName();
}
