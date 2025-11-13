package com.example.ShardedSagaWallet.repositories;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance,Long> {

}
