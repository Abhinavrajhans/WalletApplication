package com.example.ShardedSagaWallet.services.saga.steps;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.TransactionStatus;
import com.example.ShardedSagaWallet.repositories.TransactionRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateTransactionStatus implements SagaStepInterface {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext sagaContext) {
        //so inorder to update the transaction we need to we will need a transaction id
        //and we will get it from the context
        Long transactionId = sagaContext.getLong("transactionId");
        log.info("Updating transaction status for transaction {} ", transactionId);

        //find the corresponding transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("transaction not found"));

        sagaContext.put("originalTransactionStatus", transaction.getStatus());
        transaction.setStatus(TransactionStatus.SUCCESS);
        // ideally the destination transaction status which we have put TransactionStatus.SUCCESS now should
        // come from the context but for now let's say from simplicity we do it success
        // but later we can extend the functionality that we get the destinatonTransactionStatus from the context.
        transactionRepository.save(transaction);

        log.info("Transaction status updated for transaction {}", transactionId);

        sagaContext.put("transactionStatusAfterUpdate", transaction.getStatus());

        log.info("Update transaction status step executed successfully");

        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext sagaContext) {
        Long transactionId = sagaContext.getLong("transactionId");
        TransactionStatus originalTransactionStatus = TransactionStatus.valueOf(sagaContext.getString("originalTransactionStatus"));
        log.info("Compensating transaction status for transaction {} ", transactionId);

        // fetch the transaction
        Transaction transaction= transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("transaction not found"));

        transaction.setStatus(originalTransactionStatus);
        transactionRepository.save(transaction);
        log.info("Transaction status compensated for transaction {}", transactionId);
        return false;
    }

    @Override
    public String getStepName() {
        return SagaStepFactory.SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString();
    }
}
