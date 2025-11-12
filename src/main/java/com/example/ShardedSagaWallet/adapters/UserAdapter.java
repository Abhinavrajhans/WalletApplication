package com.example.ShardedSagaWallet.adapters;

import com.example.ShardedSagaWallet.dto.UserRequestDTO;
import com.example.ShardedSagaWallet.dto.UserResponseDTO;
import com.example.ShardedSagaWallet.entities.User;

public class UserAdapter {

    public static User toEntity(UserRequestDTO userRequestDTO)
    {
        return User.builder()
                .name(userRequestDTO.getName())
                .email(userRequestDTO.getEmail())
                .build();
    }

    public static UserResponseDTO toDTO(User user)
    {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();

    }
}
