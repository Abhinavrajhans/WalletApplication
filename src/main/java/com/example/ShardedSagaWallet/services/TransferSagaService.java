package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.steps.SagaOrchestrator;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransferSagaService {

    private final TransactionService transactionService;
    private final SagaOrchestrator sagaOrchestrator;


    @Transactional
    public Long initiateTransfer(
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String description
    ) {
        log.info("Initiating transfer for wallet {} to wallet {} with amount {} and description {} ", fromWalletId , toWalletId, amount, description);
        Transaction transaction = transactionService.createTransaction(fromWalletId,toWalletId,amount,description);
        //once u have created the transaction , in order to initiate the saga , u will need the saga context
        SagaContext sagaContext = SagaContext.builder()
                .data(Map.ofEntries(
                        Map.entry("transactionId",transaction.getId()),
                        Map.entry("fromWalletId",fromWalletId),
                        Map.entry("toWalletId",toWalletId),
                        Map.entry("amount",amount),
                        Map.entry("description",description)
                ))
                .build();
        // in the data we will create a map of transactionId
        Long sagaInstanceId=sagaOrchestrator.startSaga(sagaContext);
        log.info("Saga Instance created with id {}",sagaInstanceId);
        //update the sagaInstanceId
        transactionService.updateTransactionWithSagaInstanceId(transaction.getId(),sagaInstanceId);
        //we have to start execution of the saga

        exectueTransferSaga(sagaInstanceId);
        return sagaInstanceId;

        // summary
        // if u want to initiate a Transfer then
        // u are going to create a transaction
        // create a context
        // create a saga context in the db
        // update the transaction with the saga instance id
        // and then execute the transfer
    }

    @Transactional
    public void exectueTransferSaga(Long sagaInstanceId) {
        // this will only do one single thing
        log.info("Executing transfer saga with id {}",sagaInstanceId);
        // now we have to define the step that in which order the step should be executed
        try{
            for(SagaStepType step: SagaStepFactory.TransferMoneySagaSteps) {
                // here we have every single step how should we execute the steps
                boolean success= sagaOrchestrator.executeStep(sagaInstanceId,step.toString());
                if(!success)
                {
                    log.error("Failed to execute step {}",step.toString());
                    sagaOrchestrator.failSaga(sagaInstanceId);
                    return;
                    // if any step fails , then we should not mark the saga as fail
                    // we should also compensate the saga

                }
            }
            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("Transfer saga completed with id {}",sagaInstanceId);
        }catch(Exception e){
            log.info("Failed to execute transfer saga with id {}",sagaInstanceId,e);
            sagaOrchestrator.failSaga(sagaInstanceId);
        }
    }

}
