package com.example.ShardedSagaWallet.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "saga_step")
public class SagaStep extends BaseModel {

    @Column(name="saga_instance_id",nullable =false)
    private Long sagaInstanceId;

    @Column(name="step_name" , nullable = false)
    private String stepName;

    @Column(name="status",nullable=false)
    private StepStatus status;

    @Column(name="error_message" )
    private String errorMessage;

    // json step data
    @Column(name = "step_data" , columnDefinition = "json")
    private String stepData;

    public void markAsFailed() {
        this.status = StepStatus.FAILED;
    }

    public void markAsPending() {
        this.status = StepStatus.PENDING;
    }

    public void markAsRunning() {
        this.status = StepStatus.RUNNING;
    }

    public void markAsCompleted(){
        this.status = StepStatus.COMPLETED;
    }

    public void marksAsCompensating(){
        this.status = StepStatus.COMPENSATING;
    }
    public void markAsCompensated() {
        this.status = StepStatus.COMPENSATED;
    }
}
