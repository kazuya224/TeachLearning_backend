package com.kazuya.teachlearningbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
public class Session {

    @Id
    @Column(name = "session_id", columnDefinition = "uuid")
    private UUID sessionId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 3)
    private String theme;

    @Column(name = "sub_theme", nullable = false, length = 255)
    private String subTheme;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
