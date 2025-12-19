package com.kazuya.teach_learning_backend.controller;

import com.kazuya.teach_learning_backend.dto.SessionRequest;
import com.kazuya.teach_learning_backend.dto.SessionResponse;
import com.kazuya.teach_learning_backend.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * POST /sessions - チャット開始
     * リクエストを受け取り、サービス層に処理を委譲し、レスポンスを返す
     */
    @PostMapping
    public ResponseEntity<SessionResponse> startSession(@RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.startSession(request));
    }
}