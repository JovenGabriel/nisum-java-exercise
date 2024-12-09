package com.nisum.users.serviceImpl;

import com.nisum.users.dto.UserCreateDTO;
import com.nisum.users.dto.UserLoginDTO;
import com.nisum.users.entities.Phone;
import com.nisum.users.entities.User;
import com.nisum.users.exceptions.EmailAlreadyExistsException;
import com.nisum.users.exceptions.NotFoundException;
import com.nisum.users.repositories.PhoneRepository;
import com.nisum.users.repositories.UserRepository;
import com.nisum.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent())
            throw new EmailAlreadyExistsException();

        List<Phone> phones = phoneRepository.saveAll(userCreateDTO.getPhones());

        User user = User.builder()
                .name(userCreateDTO.getName())
                .email(userCreateDTO.getEmail())
                .password(userCreateDTO.getPassword())
                .phones(phones)
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        return null;
    }
}
