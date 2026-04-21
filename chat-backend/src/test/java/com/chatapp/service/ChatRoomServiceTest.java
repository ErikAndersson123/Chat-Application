package com.chatapp.service;

import com.chatapp.model.ChatRoom;
import com.chatapp.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatRoomServiceTest {

    private ChatRoomRepository chatRoomRepository;
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        chatRoomRepository = mock(ChatRoomRepository.class);
        chatRoomService = new ChatRoomService(chatRoomRepository);
    }

    @Test
    void createRoom_shouldSaveRoomWithoutPassword_whenPasswordIsBlank() {
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoom result = chatRoomService.createRoom("general", "");

        assertNotNull(result);
        assertEquals("general", result.getRoomName());
        assertNull(result.getPassword());

        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void createRoom_shouldHashPassword_whenPasswordIsProvided() {
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoom result = chatRoomService.createRoom("private-room", "secret123");

        assertNotNull(result);
        assertEquals("private-room", result.getRoomName());
        assertNotNull(result.getPassword());
        assertNotEquals("secret123", result.getPassword());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("secret123", result.getPassword()));

        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void joinRoom_shouldReturnRoom_whenRoomHasNoPassword() {
        ChatRoom room = new ChatRoom("general", null);
        when(chatRoomRepository.findById(1)).thenReturn(Optional.of(room));

        ChatRoom result = chatRoomService.joinRoom(1, null);

        assertNotNull(result);
        assertEquals("general", result.getRoomName());
        verify(chatRoomRepository).findById(1);
    }

    @Test
    void joinRoom_shouldReturnRoom_whenPasswordIsCorrect() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ChatRoom room = new ChatRoom("private-room", encoder.encode("secret123"));

        when(chatRoomRepository.findById(2)).thenReturn(Optional.of(room));

        ChatRoom result = chatRoomService.joinRoom(2, "secret123");

        assertNotNull(result);
        assertEquals("private-room", result.getRoomName());
        verify(chatRoomRepository).findById(2);
    }

    @Test
    void joinRoom_shouldThrowException_whenPasswordIsWrong() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ChatRoom room = new ChatRoom("private-room", encoder.encode("secret123"));

        when(chatRoomRepository.findById(2)).thenReturn(Optional.of(room));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> chatRoomService.joinRoom(2, "wrong"));

        assertEquals("Wrong password", ex.getMessage());
        verify(chatRoomRepository).findById(2);
    }

    @Test
    void search_shouldReturnMatchingRooms() {
        List<ChatRoom> rooms = List.of(
                new ChatRoom("general", null),
                new ChatRoom("general-2", null)
        );

        when(chatRoomRepository.findByRoomNameContainingIgnoreCase("gen")).thenReturn(rooms);

        List<ChatRoom> result = chatRoomService.search("gen");

        assertEquals(2, result.size());
        verify(chatRoomRepository).findByRoomNameContainingIgnoreCase("gen");
    }

    @Test
    void getAll_shouldReturnAllRooms() {
        List<ChatRoom> rooms = List.of(
                new ChatRoom("general", null),
                new ChatRoom("random", null)
        );

        when(chatRoomRepository.findAll()).thenReturn(rooms);

        List<ChatRoom> result = chatRoomService.getAll();

        assertEquals(2, result.size());
        verify(chatRoomRepository).findAll();
    }

    @Test
    void getById_shouldReturnRoom_whenFound() {
        ChatRoom room = new ChatRoom("general", null);
        when(chatRoomRepository.findById(5)).thenReturn(Optional.of(room));

        ChatRoom result = chatRoomService.getById(5);

        assertNotNull(result);
        assertEquals("general", result.getRoomName());
        verify(chatRoomRepository).findById(5);
    }
}