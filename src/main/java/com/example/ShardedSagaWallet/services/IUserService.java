package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.dto.UserRequestDTO;
import com.example.ShardedSagaWallet.dto.UserResponseDTO;

import java.util.List;

public interface IUserService {

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    public UserResponseDTO findUserById(Long id);
    public List<UserResponseDTO> findUserByName(String name);
}
