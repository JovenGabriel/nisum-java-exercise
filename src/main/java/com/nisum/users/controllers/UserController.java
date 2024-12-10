package com.nisum.users.controllers;

import com.nisum.users.dto.UserCreateDTO;
import com.nisum.users.dto.UserCreatedDTO;
import com.nisum.users.dto.UserLoginDTO;
import com.nisum.users.entities.User;
import com.nisum.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a list of all users.
     *
     * @return a ResponseEntity containing a list of User objects.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Retrieves a user based on the provided unique identifier (UUID).
     *
     * @param id the unique identifier of the user to retrieve
     * @return a ResponseEntity containing the User object if found, or a suitable
     *         error response if the user does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Creates a new user and returns the created user details.
     *
     * @param userCreateDTO the data transfer object containing user creation details
     * @return a ResponseEntity containing the created user's details encapsulated in a UserCreatedDTO
     */
    @PostMapping
    private ResponseEntity<UserCreatedDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User user = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserCreatedDTO.builder()
                        .id(user.getId())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .lastLogin(user.getLastLogin())
                        .token(user.getToken())
                        .isActive(user.isActive())
                        .build());
    }
    
    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param userLoginDTO the DTO containing the user's email and password for authentication
     * @return a ResponseEntity containing the authenticated User
     */
    @PostMapping("/login")
    private ResponseEntity<User> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }
    
    
}
