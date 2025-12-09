package com.kazuya.teach_learning_backend.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RelatedWeakpointId implements Serializable {
    private UUID fromKnowledgeId;
    private UUID toKnowledgeId;
}
