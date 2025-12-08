package com.kazuya.teach_learning_backend.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="users")
@Data
public class users {
    @Id
    @Column(name="user_id", columnDefinition="uuid")
    private UUID userId;

    @Column(nullalbe=false, unique=true, length=255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "session_cnt", nullable = false)
    private Integer sessionCnt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}