package com.kazuya.teach_learning_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "knowledges")
@Data
public class Knowledge {

    @Id
    @Column(name = "knowledge_id", columnDefinition = "uuid")
    private UUID knowledgeId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "session_id", columnDefinition = "uuid", nullable = false)
    private UUID sessionId;

    @Column(nullable = false, length = 255)
    private String knowledge;

    @Column(name = "weak_reason", length = 4000)
    private String weakReason;

    @Column(name = "weak_flg", length = 1)
    private String weakFlg;

    @Column(name = "study_advaice", length = 4000)
    private String studyAdvaice;

    @Column(nullable = false, length = 1)
    private String importance;

    @Column(name = "study_state", nullable = false, length = 1)
    private String studyState;

    @Column(name = "center_flg", nullable = false, length = 1)
    private String centerFlg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
