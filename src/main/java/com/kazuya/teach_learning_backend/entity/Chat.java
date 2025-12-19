package com.kazuya.teach_learning_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "chats")
@Data
public class Chat {

    @Id
    @Column(name = "chat_id", columnDefinition = "uuid")
    private UUID chatId;

    @Column(name = "session_id", columnDefinition = "uuid", nullable = false)
    private UUID sessionId;

    @Column(nullable = false, length = 4000)
    private String text;

    @Column(name = "role_flg", nullable = false, length = 1)
    private String roleFlg;

    @Column(name = "turn_number", nullable = false)
    private short turnNumber; // SMALLINT → Short が自然

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
