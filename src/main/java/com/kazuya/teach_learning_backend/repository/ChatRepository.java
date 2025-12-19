package com.kazuya.teach_learning_backend.repository;

import com.kazuya.teach_learning_backend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    // 特定のセッションのチャット履歴を取得（ターン番号昇順）
    List<Chat> findBySessionIdOrderByTurnNumberAsc(UUID sessionId);

    // 特定セッションのチャット履歴を取得（ターン番号降順）
    List<Chat> findBySessionIdOrderByTurnNumberDesc(UUID sessionId);

    // 特定セッションの最新ターン番号を取得
    @Query("SELECT MAX(c.turnNumber) FROM Chat c WHERE c.sessionId = :sessionId")
    Integer findMaxTurnNumberBySessionId(@Param("sessionId") String sessionId);

    // 特定セッションのチャット件数を取得
    long countBySessionId(UUID sessionId);

    // 特定セッションの特定ロールのチャットを取得
    List<Chat> findBySessionIdAndRoleFlgOrderByTurnNumberAsc(UUID sessionId, String roleFlg);
}
