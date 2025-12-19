package com.kazuya.teach_learning_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * GPT-4 mini を呼び出してAIの応答を取得
     * 
     * @param theme    学習テーマ
     * @param subTheme サブテーマ
     * @return AIが生成した最初の質問メッセージ
     */
    public String generateInitialQuestion(String theme, String subTheme) {
        try {
            // システムプロンプト: 家庭教師としての役割定義
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "あなたは優秀な家庭教師です。" +
                            "生徒が入力した学習テーマについて、" +
                            "いきなり解説はせず、" +
                            "まず理解度を確認する\"1つだけ\"の質問をしてください。" +
                            "質問は短く、具体的にしてください。");

            // ユーザープロンプト: 学習テーマとサブテーマの情報
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content",
                    String.format("学習テーマ: %s\nサブテーマ: %s", theme, subTheme));

            // リクエストボディ作成
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(systemMessage, userMessage));
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            // ヘッダー設定
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // OpenAI API呼び出し
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("OpenAI API error: status=" + response.getStatusCode());
            }

            // レスポンス解析
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.size() == 0) {
                JsonNode err = root.path("error");
                throw new RuntimeException("OpenAI response has no choices. error=" + err.toString());
            }

            return choices.get(0).path("message").path("content").asText();

        } catch (Exception e) {
            throw new RuntimeException("OpenAI API呼び出しに失敗しました: " + e.getMessage(), e);
        }
    }
}