package com.kazuya.teach_learning_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "related_weakpoints")
@Data
@IdClass(RelatedWeakpointId.class)
public class RelatedWeakpoint {

    @Id
    @Column(name = "from_knowledge_id", columnDefinition = "uuid")
    private UUID fromKnowledgeId;

    @Id
    @Column(name = "to_knowledge_id", columnDefinition = "uuid")
    private UUID toKnowledgeId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
