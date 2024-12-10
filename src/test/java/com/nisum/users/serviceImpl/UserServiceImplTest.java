package com.nisum.users.serviceImpl;

import com.nisum.users.dto.UserCreateDTO;
import com.nisum.users.dto.PhoneDTO;
import com.nisum.users.dto.UserLoginDTO;
import com.nisum.users.entities.User;
import com.nisum.users.exceptions.EmailAlreadyExistsException;
import com.nisum.users.exceptions.NotFoundException;
import com.nisum.users.repositories.UserRepository;
import com.nisum.users.repositories.PhoneRepository;
import com.nisum.users.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userServiceImpl;

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccessfully() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john.doe@example.com");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(Optional.empty());
        when(jwtTokenUtil.generateToken(userCreateDTO.getEmail())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User createdUser = userServiceImpl.createUser(userCreateDTO);

        // Assert
        assertNotNull(createdUser);
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());
        assertNotEquals("Password123!", createdUser.getPassword());
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getUpdatedAt());
        assertNotNull(createdUser.getLastLogin());
        assertTrue(createdUser.isActive());
        assertEquals(1, createdUser.getPhones().size());

        assertNotNull(createdUser.getToken());
    }

    @Test
    void testCreateUserThrowsEmailAlreadyExistsException() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john.doe@example.com");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        userServiceImpl.createUser(userCreateDTO);

        UserCreateDTO userCreateDTO2 = new UserCreateDTO();
        userCreateDTO2.setName("John Doe");
        userCreateDTO2.setEmail("john.doe@example.com");
        userCreateDTO2.setPassword("Password123!");

        List<PhoneDTO> phones2 = new ArrayList<>();
        phones2.add(new PhoneDTO("987654321", "123", "1"));
        userCreateDTO2.setPhones(phones2);


        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userServiceImpl.createUser(userCreateDTO2));
    }

    @Test
    void testLoginSuccessfully() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john.doe@example.com");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        userServiceImpl.createUser(userCreateDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("john.doe@example.com");
        userLoginDTO.setPassword("Password123!");

        // Act
        User loggedInUser = userServiceImpl.login(userLoginDTO);

        // Assert
        assertNotNull(loggedInUser);
        assertNotNull(loggedInUser.getToken());
    }

    @Test
    void testLoginWithInvalidEmailThrowsNotFoundException() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("invalid@example.com");
        userLoginDTO.setPassword("Password123!");

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userServiceImpl.login(userLoginDTO));
    }

    @Test
    void testLoginWithInvalidPasswordThrowsNotFoundException() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("john.doe@example.com");
        userLoginDTO.setPassword("WrongPassword!");

        User user = User.builder()
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("Password123!"))
                .build();

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userServiceImpl.login(userLoginDTO));
    }
}