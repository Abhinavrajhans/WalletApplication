package com.example.ShardedSagaWallet.config;


import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import com.example.ShardedSagaWallet.services.saga.steps.CreditDestinationWalletStep;
import com.example.ShardedSagaWallet.services.saga.steps.DebitSourceWalletStep;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;
import com.example.ShardedSagaWallet.services.saga.steps.UpdateTransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SagaConfiguration {

    @Bean
    public Map<String, SagaStepInterface> sagaStepMap(
            DebitSourceWalletStep debitSourceWalletStep,
            CreditDestinationWalletStep creditDestinationWalletStep,
            UpdateTransactionStatus updateTransactionStatus
    )
    {
            Map<String, SagaStepInterface> map = new HashMap<String, SagaStepInterface>();
            map.put(SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString(),debitSourceWalletStep);
            map.put(SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString(),creditDestinationWalletStep);
            map.put(SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString(),updateTransactionStatus);
            return map;
    }
}
