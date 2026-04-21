package com.chatapp.repository;

import com.chatapp.model.Message;
import com.chatapp.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByRoomOrderByTimestampAsc(ChatRoom room);
}