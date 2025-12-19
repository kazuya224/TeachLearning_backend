package com.kazuya.teach_learning_backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SessionResponse {
    private Integer responseStatus;
    private UUID sessionId;
}
