package com.example.ShardedSagaWallet.services.saga;

public interface SagaStepInterface {

    boolean execute(SagaContext sagaContext);

    boolean compensate(SagaContext sagaContext);

    String getStepName();
}
