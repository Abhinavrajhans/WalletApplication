package com.example.ShardedSagaWallet.repositories;

import com.example.ShardedSagaWallet.services.saga.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    // we should be able to find all the saga steps belong to a sagainstanceid
    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    // every saga step has a status
    // find completed state of a sagainstance id
    // it would be better to fing the completed step as we might need to compensate them
    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = :status")
    List<SagaStep> findCompletedStepsBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId, @Param("status") String status);


    // we want to have completed or compensated so this status query can be a in query
    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = IN ('COMPLETED' , 'COMPENSATED')")
    List<SagaStep> findCompletedorCompensatedStepsBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

}
