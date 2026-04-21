package com.chatapp.controller;

import com.chatapp.model.ChatRoom;
import com.chatapp.service.ChatRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class ChatRoomController {

    private final ChatRoomService service;

    // Inject ChatRoomService to handle room-related logic
    public ChatRoomController(ChatRoomService service) {
        this.service = service;
    }

    // Create a new chat room
    @PostMapping("/create")
    public ChatRoom create(
            @RequestParam String name,
            @RequestParam(required = false) String password
    ) {
        return service.createRoom(name, password);
    }

    // Join an existing room
    @PostMapping("/join")
    public ChatRoom join(
            @RequestParam Integer id,
            @RequestParam(required = false) String password
    ) {
        return service.joinRoom(id, password);
    }

    // Search for rooms
    @GetMapping("/search")
    public List<ChatRoom> search(@RequestParam String q) {
        return service.search(q);
    }

    // Get all available chatrooms
    @GetMapping
    public List<ChatRoom> all() {
        return service.getAll();
    }
}