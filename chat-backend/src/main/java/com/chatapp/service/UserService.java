package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Inject user repository and password encoder
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user and store a hashed password
    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String hashed = passwordEncoder.encode(password);
        User user = new User(username, hashed);

        return userRepository.save(user);
    }

    // Validate login credentials and return the user if correct
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // Fetch a user by id
    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }
}