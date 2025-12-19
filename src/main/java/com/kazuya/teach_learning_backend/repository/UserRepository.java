package com.kazuya.teach_learning_backend.repository;

import com.kazuya.teach_learning_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // メールアドレスでユーザーを検索
    Optional<User> findByEmail(String email);

    // メールアドレスの重複チェック
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.sessionCnt = u.sessionCnt + 1 WHERE u.userId = :userId")
    void incrementSessionCount(@Param("userId") UUID userId);

    @Query("SELECT u.sessionCnt FROM User u WHERE u.userId = :userId")
    Integer getSessionCount(@Param("userId") String userId);
}