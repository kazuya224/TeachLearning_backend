package com.kazuya.teach_learning_backend.service;

import com.kazuya.teach_learning_backend.dto.LoginRequest;
import com.kazuya.teach_learning_backend.dto.LoginResponse;
import com.kazuya.teach_learning_backend.dto.SignupRequest;
import com.kazuya.teach_learning_backend.dto.SignupResponse;
import com.kazuya.teach_learning_backend.entity.User;
import com.kazuya.teach_learning_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        // 1. メールアドレスでユーザーを検索
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("メールアドレスまたはパスワードが正しくありません。"));

        // 2. パスワード照合（生パスワード vs 保存されているハッシュ）
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("メールアドレスまたはパスワードが正しくありません。");
        }

        // 3. 成功したらレスポンスを返す
        return new LoginResponse(
                1,
                user.getUserId(),
                user.getName(),
                user.getEmail());
    }

    // ユーザー登録時に使う想定（あとでサインアップ実装するときに使えます）
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 新規登録
    public SignupResponse signup(SignupRequest request) {
        log.info("signup処理開始 - email: {}, password: {}, name {}", request.getEmail(), request.getPassword(),
                request.getName());
        SignupResponse response = new SignupResponse();
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("このメールアドレスは既に登録されています");
            }

            User user = new User();
            user.setUserId(UUID.randomUUID());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setName(request.getEmail());
            user.setSessionCnt(0);

            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            userRepository.save(user);

            response.setResponseStatus(1);
        } catch (Exception e) {
            response.setResponseStatus(0);
        }
        return response;
    }
}
