package com.chatapp.controller;

import com.chatapp.model.*;
import com.chatapp.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // Inject dependencies for messaging and database access
    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   MessageRepository messageRepository,
                                   UserRepository userRepository,
                                   ChatRoomRepository chatRoomRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // Handle incoming chat messages, save them, and broadcast to the room
    @MessageMapping("/chat")
    public void send(Message message) {

        User sender = userRepository.findById(message.getSender().getId())
                .orElseThrow();

        ChatRoom room = chatRoomRepository.findById(message.getRoom().getId())
                .orElseThrow();

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setRoom(room);
        newMessage.setContent(message.getContent());
        newMessage.setTimestamp(LocalDateTime.now());

        Message saved = messageRepository.save(newMessage);

        messagingTemplate.convertAndSend(
                "/topic/room/" + room.getId(),
                saved
        );
    }

    // Notify clients in the room that a user is currently typing
    @MessageMapping("/typing")
    public void typing(Message message) {

        User sender = userRepository.findById(message.getSender().getId())
                .orElseThrow();

        ChatRoom room = chatRoomRepository.findById(message.getRoom().getId())
                .orElseThrow();

        messagingTemplate.convertAndSend(
                "/topic/room/" + room.getId() + "/typing",
                sender.getUsername()
        );
    }
}