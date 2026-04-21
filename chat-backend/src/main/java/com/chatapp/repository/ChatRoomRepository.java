package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    List<ChatRoom> findByRoomName(String roomName);

    List<ChatRoom> findByRoomNameContainingIgnoreCase(String query);
}