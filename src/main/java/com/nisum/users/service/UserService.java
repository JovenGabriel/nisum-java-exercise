package com.nisum.users.service;

import com.nisum.users.dto.UserCreateDTO;
import com.nisum.users.dto.UserLoginDTO;
import com.nisum.users.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> getUsers();
    User getUserById(UUID id);
    User createUser(UserCreateDTO userCreateDTO);
    User login(UserLoginDTO userLoginDTO);
}
