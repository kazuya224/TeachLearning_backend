package com.kazuya.teach_learning_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Integer responseStatus;
    private UUID userId;
    private String name;
    private String email;
}
