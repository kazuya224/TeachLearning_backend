package com.kazuya.teach_learning_backend.service;

import com.kazuya.teach_learning_backend.dto.LoginRequest;
import com.kazuya.teach_learning_backend.dto.LoginResponse;
import com.kazuya.teach_learning_backend.entity.User;
import com.kazuya.teach_learning_backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
