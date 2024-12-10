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
import com.nisum.users.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Retrieves a list of all users from the user repository.
     *
     * @return a list containing all User entities present in the system
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to be retrieved
     * @return the User object associated with the specified UUID
     * @throws NotFoundException if no user is found with the given UUID
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Override
    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent())
            throw new EmailAlreadyExistsException();

        List<Phone> phones = userCreateDTO.getPhones().stream().map((phoneDTO) -> Phone.builder()
                        .number(phoneDTO.getNumber())
                        .cityCode(phoneDTO.getCitycode())
                        .countryCode(phoneDTO.getCountrycode())
                        .build())
                        .collect(Collectors.toList());

        phoneRepository.saveAll(phones);

        User user = User.builder()
                .name(userCreateDTO.getName())
                .email(userCreateDTO.getEmail())
                .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                .phones(phones)
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .token(jwtTokenUtil.generateToken(userCreateDTO.getEmail()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    @Transactional
    public User login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));

        if (passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            String token = jwtTokenUtil.generateToken(user.getEmail());
            user.setToken(token);
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new NotFoundException("Invalid email or password");
        }
    }
}
