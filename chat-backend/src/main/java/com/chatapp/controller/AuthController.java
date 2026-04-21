package com.chatapp.controller;

import com.chatapp.model.User;
import com.chatapp.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    // Inject UserService to handle auth logic
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Handle user registration requests
    @PostMapping("/register")
    public User register(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return userService.register(username, password);
    }

    // Handle user login requests
    @PostMapping("/login")
    public User login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return userService.login(username, password);
    }
}