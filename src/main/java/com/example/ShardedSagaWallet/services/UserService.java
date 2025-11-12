package com.example.ShardedSagaWallet.services;

import com.example.ShardedSagaWallet.adapters.UserAdapter;
import com.example.ShardedSagaWallet.dto.UserRequestDTO;
import com.example.ShardedSagaWallet.dto.UserResponseDTO;
import com.example.ShardedSagaWallet.entities.User;
import com.example.ShardedSagaWallet.repositories.UserRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info("Created user:{}", userRequestDTO.getEmail());
        User savedUser =userRepository.save(UserAdapter.toEntity(userRequestDTO)) ;
        log.info("User created with id {} in database shardwallet{} ", savedUser.getId(),(savedUser.getId() % 2 + 1));
        return UserAdapter.toDTO(savedUser);
    }

    @Override
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User does not exist"));
        return UserAdapter.toDTO(user);
    }

    @Override
    public List<UserResponseDTO> findUserByName(String name)
    {
        return this.userRepository.findByNameContainingIgnoreCase(name).stream().map(UserAdapter::toDTO).collect(Collectors.toList());
    }




}
