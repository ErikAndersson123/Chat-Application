package com.chatapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String roomName;

    @Column
    private String password;

    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Message> messages;

    public ChatRoom() {}

    public ChatRoom(String roomName, String password) {
        this.roomName = roomName;
        this.password = password;
        this.timestamp = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}