package com.example.ShardedSagaWallet.repositories;

import com.example.ShardedSagaWallet.entities.SagaStep;
import com.example.ShardedSagaWallet.entities.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    // we should be able to find all the saga steps belong to a sagainstanceid
    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    //using this method what we can do is we can pass the sagainstanceId and status
    // inorder to fetch the sagastep this will give us more granular control
    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId , StepStatus status);


    // find by sagainstanceId and
    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaInstanceId , String stepNam,StepStatus status);



    // every saga step has a status
    // find completed state of a sagainstance id
    // it would be better to fing the completed step as we might need to compensate them
    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = com.example.ShardedSagaWallet.entities.StepStatus.COMPLETED")
    List<SagaStep> findCompletedStepsBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);


//    // we want to have completed or compensated so this status query can be a in query
//    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = IN ('COMPLETED' , 'COMPENSATED')")
//    List<SagaStep> findCompletedorCompensatedStepsBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

}
