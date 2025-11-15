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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator{

    private final ObjectMapper objectMapper;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepFactory sagaStepFactory;
    private final SagaStepRepository sagaStepRepository;

    @Override
    @Transactional
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
    @Transactional
    public boolean executeStep(Long sagaInstanceId, String stepName) {
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(()->new RuntimeException("SagaInstance not found"));

        SagaStepInterface step=sagaStepFactory.getSagaStep(stepName);
        if(step==null){
            throw new RuntimeException("SagaStepInterface not found");
        }

        SagaStep sagaStepDB= sagaStepRepository.
                        findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId,stepName,StepStatus.PENDING)
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
            sagaStepDB.markAsRunning();
            sagaStepDB = sagaStepRepository.save(sagaStepDB); // updating the status to running in db.

            boolean success = step.execute(sagaContext);
            if(success){
                sagaStepDB.markAsCompleted();
                sagaStepRepository.save(sagaStepDB); // updating the status to completed in db.

                sagaInstance.setCurrentStep(stepName); // step we just executed
                sagaInstance.markAsRunning();
                sagaInstanceRepository.save(sagaInstance); // updating the status to running in db.
                // so that let's say if we want to resume from anywhere we know that this step has been
                // complete , saga is not completed it is still in the running state and that is what
                // we are fetching from the db

                log.info("Saga {} executed successfully", stepName);
                return true;
            }
            else{
                sagaStepDB.markAsFailed();
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
            sagaStepDB.markAsFailed();
            sagaStepRepository.save(sagaStepDB);
            log.error("Failed to execute the step {}", stepName , e);
            return false;
        }
    }

    @Override
    public boolean compensateStep(Long sagaInstanceId, String stepName) {
        // 1. Fetch the saga instance from db using the saga instance id
        // 2. Fetch the saga step from db using the saga instance id and step name
        // 3. Take the context from saga instance and call the compensate method of the step
        // 4. Update the appropritate status in the saga step
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(()->new RuntimeException("SagaInstance not found"));

        SagaStepInterface step=sagaStepFactory.getSagaStep(stepName);
        if(step==null){
            throw new RuntimeException("SagaStepInterface not found");
        }

        //now when we want to compensate the step we will not look for the status PENDING
        // we want to look for the status completed because we will only compensate the completed steps
        // or if there are no completed steps then we will not do anything and keep it null.
        SagaStep sagaStepDB= sagaStepRepository.
                findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId,stepName,StepStatus.COMPLETED)
                .orElse(null);


        // if there is no completed step found in the db
        // what do we do , we don't need to save anything in the db we will return true
        // there is no step of compensate and the function has succesffully completed.
        if(sagaStepDB==null){
            log.info("Step {} not found in the db for saga instance {} , so it is already compensated", stepName , sagaInstanceId);
            return true;
        }

        // get the context and store in the sagacontext
        try{
            SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
            // above line helps to convert in the form of the class which SagaContext.class
            sagaStepDB.marksAsCompensating();
            sagaStepDB = sagaStepRepository.save(sagaStepDB); // updating the status to running in db.

            //now we have to compensate the step
            // how do we compensate the step ?
            // every step knows how to compensate itself.
            boolean success = step.compensate(sagaContext);
            if(success){
                sagaStepDB.markAsCompensated();
                sagaStepRepository.save(sagaStepDB); // updating the status to completed in db.
                log.info("Saga {} compensated successfully", stepName);
                return true;
            }
            else{
                sagaStepDB.markAsFailed();
                sagaStepDB = sagaStepRepository.save(sagaStepDB); // updating the status to failed in db.
                log.error("Saga {} compensation failed", stepName);
                return false;
            }
        }
        catch(Exception e){
            sagaStepDB.markAsFailed();
            sagaStepRepository.save(sagaStepDB);
            log.error("Failed to execute the step {}", stepName , e);
            return false;
        }
    }

    @Override
    public SagaInstance getSagaInstance(Long sagaInstanceId) {
        return sagaInstanceRepository.findById(sagaInstanceId).
                orElseThrow(()->new RuntimeException("SagaInstance not found"));
    }

    @Override
    public void compensateSaga(Long sagaInstanceId) {
        // 1. Fetch the saga instance from db using the saga instance id
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(
                ()->new RuntimeException("SagaInstance not found")
        );
        // mark the saga status as compensating in db
        sagaInstance.markAsCompensating();
        sagaInstanceRepository.save(sagaInstance);

        // get all the completed steps
        List<SagaStep> completedSteps = sagaStepRepository.findCompletedStepsBySagaInstanceId(sagaInstanceId);

        boolean allCompensated = true;
        for(SagaStep completedStep:completedSteps){
            // we will go to every step one by one and start compensating
            boolean compensated = this.compensateStep(sagaInstanceId, completedStep.getStepName());
            // but maybe while compensation something goes wrong
            // so we need to keep a track
            if(!compensated){
                allCompensated = false;
            }
        }

        if(allCompensated){
            sagaInstance.markAsCompensated();
            sagaInstanceRepository.save(sagaInstance);
            log.info("Saga {} compensated successfully", sagaInstanceId);
        }
        else{
            log.error("Saga {} compensation failed", sagaInstanceId);
        }

    }

    @Override
    public void failSaga(Long sagaInstanceId) {
            SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                    .orElseThrow(()->new RuntimeException("SagaInstance not found"));
            sagaInstance.markAsFailed();
            sagaInstanceRepository.save(sagaInstance);
    }

    @Override
    public void completeSaga(Long sagaInstanceId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).
                orElseThrow(()->new RuntimeException("SagaInstance not found"));
        sagaInstance.markAsCompleted();
        sagaInstanceRepository.save(sagaInstance);
    }
}
