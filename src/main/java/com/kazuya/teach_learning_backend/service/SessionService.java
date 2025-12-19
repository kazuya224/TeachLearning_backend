package com.kazuya.teach_learning_backend.service;

import com.kazuya.teach_learning_backend.dto.ChatResponse;
import com.kazuya.teach_learning_backend.dto.ChatRequest;
import com.kazuya.teach_learning_backend.dto.SessionRequest;
import com.kazuya.teach_learning_backend.dto.SessionResponse;
import com.kazuya.teach_learning_backend.entity.Chat;
import com.kazuya.teach_learning_backend.entity.Session;
import com.kazuya.teach_learning_backend.service.OpenAIService;
import com.kazuya.teach_learning_backend.repository.ChatRepository;
import com.kazuya.teach_learning_backend.repository.SessionRepository;
import com.kazuya.teach_learning_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    /**
     * セッション開始処理（コントローラーから呼ばれる）
     * 
     * @param userId  ログインユーザーID
     * @param request リクエストDTO（theme, subTheme）
     * @return レスポンスDTO（responseStatus, sessionId）
     */
    @Transactional
    public SessionResponse startSession(SessionRequest request) {
        UUID userId = request.getUserId();
        if (userId == null) {
            SessionResponse res = new SessionResponse();
            res.setResponseStatus(0);
            res.setSessionId(null);
            return res;
        }

        UUID sessionId = createChatSession(userId, request.getTheme(), request.getSubTheme());

        SessionResponse res = new SessionResponse();
        res.setResponseStatus(1);
        res.setSessionId(sessionId);
        return res;
    }

    /**
     * チャットセッション作成の内部処理
     * 1. セッションIDを生成
     * 2. 新規セッションを作成（ユーザーID、テーマ、サブテーマを登録）
     * 3. ユーザーのセッション数をインクリメント
     * 4. AIによる最初の質問を生成
     * 5. 初回AIメッセージを保存
     * 
     * @param userId   ログインユーザーID
     * @param theme    学習テーマ
     * @param subTheme サブテーマ
     * @return 作成されたセッションID
     */
    private UUID createChatSession(UUID userId, String theme, String subTheme) {
        // 1. セッションIDを生成
        UUID sessionId = UUID.randomUUID();

        // 2. 新規セッションを作成してデータを登録
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setTheme(theme);
        session.setSubTheme(subTheme);
        sessionRepository.save(session);

        // 3. ユーザーテーブルのセッション数をインクリメント
        userRepository.incrementSessionCount(userId);

        // 4. AIによる最初の質問を生成
        String aiQuestion = openAIService.generateInitialQuestion(theme, subTheme);

        // 5. 初回AIメッセージをCHATSテーブルに保存
        UUID chatId = UUID.randomUUID();
        Chat chat = new Chat();
        chat.setChatId(chatId);
        chat.setSessionId(sessionId);
        chat.setText(aiQuestion);
        chat.setRoleFlg("0"); // 0 = AI
        chat.setTurnNumber((short) 1); // 初回ターン
        chatRepository.save(chat);

        return sessionId;
    }

    /**
     * ユーザーの現在のセッション数を取得
     * 
     * @param userId ユーザーID
     * @return セッション数
     */
    public Integer getUserSessionCount(String userId) {
        return userRepository.getSessionCount(userId);
    }

    @Transactional
    public ChatResponse chatMessage(UUID userId, ChatRequest request) {
        if (userId == null || request == null) {
            return new ChatResponse(0, (short) 0, null);
        }

        UUID sessionId = request.sessionId();
        String userText = request.text();

        if (sessionId == null || userText == null || userText.isBlank()) {
            return new ChatResponse(0, (short) 0, null);
        }

        // セッション存在 & 所有チェック
        Session session = sessionRepository.findBySessionIdAndUserId(sessionId, userId).orElse(null);
        if (session == null) {
            return new ChatResponse(0, (short) 0, null);
        }

        Integer maxTurn = chatRepository.findMaxTurnNumberBySessionId(sessionId);
        int nextTurn = (maxTurn == null ? 1 : maxTurn + 1);

        // USER保存（1=USER）
        Chat userChat = new Chat();
        userChat.setChatId(UUID.randomUUID());
        userChat.setSessionId(sessionId);
        userChat.setText(userText);
        userChat.setRoleFlg("1");
        userChat.setTurnNumber((short) nextTurn);
        chatRepository.save(userChat);

        // OpenAIへ渡す履歴
        List<Map<String, String>> messages = buildMessagesForOpenAi(sessionId, session);

        // AI生成
        String aiReply = openAIService.generateReply(messages);

        // AI保存（0=AI）
        Chat aiChat = new Chat();
        aiChat.setChatId(UUID.randomUUID());
        aiChat.setSessionId(sessionId);
        aiChat.setText(aiReply);
        aiChat.setRoleFlg("0");
        aiChat.setTurnNumber((short) (nextTurn + 1));
        chatRepository.save(aiChat);

        return new ChatResponse(1, (short) (nextTurn + 1), aiReply);
    }

    private List<Map<String, String>> buildMessagesForOpenAi(UUID sessionId, Session session) {
        // 最新20件（コスト対策）
        List<Chat> historyDesc = chatRepository.findBySessionIdOrderByTurnNumberDesc(sessionId);
        List<Chat> limited = historyDesc.stream().limit(20).toList();

        List<Chat> historyAsc = new ArrayList<>(limited);
        Collections.reverse(historyAsc);

        List<Map<String, String>> messages = new ArrayList<>();

        String systemPrompt = "あなたは優秀な家庭教師です。"
                + "生徒の理解度を確認しながら、いきなり解説はせず、まず短く具体的な質問を1つだけしてください。"
                + "必要に応じてヒントを出してください。\n"
                + "学習テーマ: " + safe(session.getTheme()) + "\n"
                + "サブテーマ: " + safe(session.getSubTheme());

        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (Chat c : historyAsc) {
            String role = "1".equals(c.getRoleFlg()) ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", safe(c.getText())));
        }
        return messages;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}