package com.kazuya.teach_learning_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kazuya.teach_learning_backend.entity.Session;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    // 特定のユーザーのセッション一覧を取得（更新日時順）
    List<Session> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    // 特定のユーザーの特定のセッションを取得
    Optional<Session> findBySessionIdAndUserId(UUID sessionId, UUID userId);

    // 特定のテーマのセッション一覧を取得
    List<Session> findByUserIdAndThemeOrderByUpdatedAtDesc(UUID userId, String theme);
}
