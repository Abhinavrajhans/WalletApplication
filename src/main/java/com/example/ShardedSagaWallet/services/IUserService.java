package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.dto.UserRequestDTO;
import com.example.ShardedSagaWallet.dto.UserResponseDTO;

public interface IUserService {

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    public UserResponseDTO getUserById(Long id);
}
