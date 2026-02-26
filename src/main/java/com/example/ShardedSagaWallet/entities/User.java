package com.example.ShardedSagaWallet.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Entity
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel{

    @NotBlank(message ="name is required")
    @Size(min=3 , max=100 , message="name character must be between 3 and 100")
    private String name;

    @NotBlank(message ="email is required")
    @Email(message="email is invalid")
    private String email;

}
