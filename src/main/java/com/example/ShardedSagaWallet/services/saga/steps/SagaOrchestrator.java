package com.example.ShardedSagaWallet.services.saga.steps;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import com.example.ShardedSagaWallet.services.saga.SagaContext;

public interface SagaOrchestrator {

    Long startSaga(SagaContext sagaContext);

    boolean executeStep(Long sagaInstanceId , String stepName);

    boolean compensateStep(Long sagaInstanceId , String stepName);

    SagaInstance getSagaInstance(Long sagaInstanceId);

    void compensateSaga(Long sagaInstanceId);

    void failSaga(Long sagaInstanceId);

    void completeSaga(Long sagaInstanceId);
}
