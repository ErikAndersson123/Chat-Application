package com.chatapp.service;

import com.chatapp.model.ChatRoom;
import com.chatapp.repository.ChatRoomRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Inject repository for database operations
    public ChatRoomService(ChatRoomRepository repo) {
        this.repo = repo;
    }

    // Create a room and hash the password if provided
    public ChatRoom createRoom(String name, String password) {
        String hashed = (password == null || password.isBlank())
                ? null
                : encoder.encode(password);

        return repo.save(new ChatRoom(name, hashed));
    }

    // Join a room, validating password if the room is protected
    public ChatRoom joinRoom(Integer id, String password) {
        ChatRoom room = repo.findById(id).orElseThrow();

        if (room.getPassword() != null) {
            if (password == null || !encoder.matches(password, room.getPassword())) {
                throw new RuntimeException("Wrong password");
            }
        }

        return room;
    }

    // Search rooms by name (case-insensitive)
    public List<ChatRoom> search(String query) {
        return repo.findByRoomNameContainingIgnoreCase(query);
    }

    // Return all rooms
    public List<ChatRoom> getAll() {
        return repo.findAll();
    }

    // Get a single room by id
    public ChatRoom getById(Integer id) {
        return repo.findById(id).orElseThrow();
    }
}