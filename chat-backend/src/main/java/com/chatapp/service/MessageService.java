package com.chatapp.service;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    // Inject repository for database operations
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Create and store a new message
    public Message sendMessage(User user, ChatRoom room, String content) {
        Message message = new Message(user, room, content);
        return messageRepository.save(message);
    }

    // Retrieve all messages for a room in chronological order
    public List<Message> getMessagesByRoom(ChatRoom room) {
        return messageRepository.findByRoomOrderByTimestampAsc(room);
    }
}