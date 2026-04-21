package com.chatapp.service;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    private MessageRepository messageRepository;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        messageService = new MessageService(messageRepository);
    }

    @Test
    void sendMessage_shouldCreateAndSaveMessage() {
        User user = new User("bob", "hash");
        ChatRoom room = new ChatRoom("general", null);

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message result = messageService.sendMessage(user, room, "Hello world");

        assertNotNull(result);
        assertEquals("Hello world", result.getContent());
        assertEquals(user, result.getSender());
        assertEquals(room, result.getRoom());
        assertNotNull(result.getTimestamp());

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void getMessagesByRoom_shouldReturnMessagesInRoom() {
        ChatRoom room = new ChatRoom("general", null);
        User user = new User("bob", "hash");

        Message first = new Message(user, room, "First");
        Message second = new Message(user, room, "Second");

        when(messageRepository.findByRoomOrderByTimestampAsc(room)).thenReturn(List.of(first, second));

        List<Message> result = messageService.getMessagesByRoom(room);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getContent());
        assertEquals("Second", result.get(1).getContent());

        verify(messageRepository).findByRoomOrderByTimestampAsc(room);
    }
}