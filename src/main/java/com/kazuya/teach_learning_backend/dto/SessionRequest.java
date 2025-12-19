package com.kazuya.teach_learning_backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SessionRequest {
    private UUID userId;
    private String theme;
    private String subTheme;
}
