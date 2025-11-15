package com.example.ShardedSagaWallet.services.saga.steps;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import com.example.ShardedSagaWallet.entities.SagaStatus;
import com.example.ShardedSagaWallet.entities.SagaStep;
import com.example.ShardedSagaWallet.entities.StepStatus;
import com.example.ShardedSagaWallet.repositories.SagaInstanceRepository;
import com.example.ShardedSagaWallet.repositories.SagaStepRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator{

    private final ObjectMapper objectMapper;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepFactory sagaStepFactory;
    private final SagaStepRepository sagaStepRepository;

    @Override
    public Long startSaga(SagaContext sagaContext) {
        try{
            String contextJson  = objectMapper.writeValueAsString(sagaContext);
            // this line will convert the java sagaContext to a json as a string.
            SagaInstance sagaInstance = SagaInstance.builder()
                    .context(contextJson)
                    .status(SagaStatus.STARTED)
                    .build();
            sagaInstance =  sagaInstanceRepository.save(sagaInstance);
            log.info("Started saga with Id {}", sagaInstance.getId());
            return  sagaInstance.getId();
        }
        catch(Exception e){
            log.error("Error starting saga", e);
            throw new RuntimeException("Error starting saga", e);
        }
    }

    @Override
    public boolean executeStep(Long sagaInstanceId, String stepName) {
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(()->new RuntimeException("SagaInstance not found"));

        SagaStepInterface step=sagaStepFactory.getSagaStep(stepName);
        if(step==null){
            throw new RuntimeException("SagaStepInterface not found");
        }

        SagaStep sagaStepDB=sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.PENDING).stream()
                .filter(s-> s.getStepName().equals(stepName))
                .findFirst()
                .orElse(SagaStep.builder()
                        .sagaInstanceId(sagaInstanceId)
                        .stepName(stepName)
                        .status(StepStatus.PENDING)
                        .build()
                );
        //incase the object is not present in the database then we create a new object

        if(sagaStepDB.getId()==null){
            // if in the else we are creating a new object then it will not have id.
            // that means we need to save the saga object
            sagaStepDB = sagaStepRepository.save(sagaStepDB);
        }

        // after the step object
        // now we should try to either create the saga step in our database
        // or incase if it is not created then we should try to create it.
        // if it is already present in the db then good if not then create a instance in db.

        // check if it is present in the db or not
        // either create or get the existing saga step
        // we have not started the execution yet ,let's start the execution
        // for a step to be executed the step might need what , inorder for the step to be executed
        // the step need a context and who has the context ? -> instance has the context in the
        // form of a string , we can use the objectMapper and get the context object here.

        try{
            SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
            // above line helps to convert in the form of the class which SagaContext.class
            sagaStepDB.setStatus(StepStatus.RUNNING);
            sagaStepDB = sagaStepRepository.save(sagaStepDB); // updating the status to running in db.

            boolean success = step.execute(sagaContext);
            if(success){
                sagaStepDB.setStatus(StepStatus.COMPLETED);
                sagaStepRepository.save(sagaStepDB); // updating the status to completed in db.

                sagaInstance.setCurrentStep(stepName); // step we just executed
                sagaInstance.setStatus(SagaStatus.RUNNING);
                sagaInstanceRepository.save(sagaInstance); // updating the status to running in db.
                // so that let's say if we want to resume from anywhere we know that this step has been
                // complete , saga is not completed it is still in the running state and that is what
                // we are fetching from the db

                log.info("Saga {} executed successfully", stepName);
                return true;
            }
            else{
                sagaStepDB.setStatus(StepStatus.FAILED);
                sagaStepDB = sagaStepRepository.save(sagaStepDB); // updating the status to failed in db.
                log.error("Saga {} execution failed", stepName);
                return false;
            }
            //now we have updated the step , we should also update the saga instance
            // we took the context from the sagainstance
            // we started executing the step
            // if the step was successful , u update the step and return true
            // else u update the step and return false
            // we will update the saga instance now
            // executing the step is just calling the execute function rest all is the preparation .
        }
        catch(Exception e){

            // in the catch if anything goes wrong
            // what we should do as we should mark the saga step has failed
            sagaStepDB.setStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);
            log.error("Failed to execute the step {}", stepName , e);
            return false;
        }
    }

    @Override
    public boolean compensateStep(Long sagaInstanceId, String stepName) {
        return false;
    }

    @Override
    public SagaInstance getSagaInstance(Long sagaInstanceId) {
        return null;
    }

    @Override
    public void compensateSaga(Long sagaInstanceId) {

    }

    @Override
    public void failSaga(Long sagaInstanceId) {

    }

    @Override
    public void completeSaga(Long sagaInstanceId) {

    }
}
