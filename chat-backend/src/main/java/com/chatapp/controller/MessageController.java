package com.chatapp.controller;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.ChatRoomService;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final ChatRoomService roomService;

    // Inject services for handling messages, users, and rooms
    public MessageController(
            MessageService messageService,
            UserService userService,
            ChatRoomService roomService
    ) {
        this.messageService = messageService;
        this.userService = userService;
        this.roomService = roomService;
    }

    // Send a message to a specific room from a user
    @PostMapping
    public Message sendMessage(
            @RequestParam Integer userId,
            @RequestParam Integer roomId,
            @RequestParam String content
    ) {
        User user = userService.getById(userId).orElseThrow();
        ChatRoom room = roomService.getById(roomId);

        return messageService.sendMessage(user, room, content);
    }

    // Retrieve all messages for a given room
    @GetMapping("/room/{roomId}")
    public List<Message> getMessages(@PathVariable Integer roomId) {
        ChatRoom room = roomService.getById(roomId);
        return messageService.getMessagesByRoom(room);
    }
}