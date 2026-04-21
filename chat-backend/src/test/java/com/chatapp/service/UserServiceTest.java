package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void register_shouldSaveUser_whenUsernameDoesNotExist() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register("bob", "password123");

        assertNotNull(result);
        assertEquals("bob", result.getUsername());
        assertNotNull(result.getPasswordHash());
        assertNotEquals("password123", result.getPasswordHash());
        assertTrue(passwordEncoder.matches("password123", result.getPasswordHash()));

        verify(userRepository).findByUsername("bob");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.register("bob", "password123"));

        assertEquals("User already exists", ex.getMessage());
        verify(userRepository).findByUsername("bob");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnUser_whenPasswordMatches() {
        User user = new User("bob", passwordEncoder.encode("password123"));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        User result = userService.login("bob", "password123");

        assertNotNull(result);
        assertEquals("bob", result.getUsername());
        verify(userRepository).findByUsername("bob");
    }

    @Test
    void login_shouldThrowException_whenPasswordDoesNotMatch() {
        User user = new User("bob", passwordEncoder.encode("password123"));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("bob", "wrong-password"));

        assertEquals("Invalid password", ex.getMessage());
        verify(userRepository).findByUsername("bob");
    }

    @Test
    void getById_shouldReturnUser_whenFound() {
        User user = new User("bob", "hash");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getById(1);

        assertTrue(result.isPresent());
        assertEquals("bob", result.get().getUsername());
        verify(userRepository).findById(1);
    }
}