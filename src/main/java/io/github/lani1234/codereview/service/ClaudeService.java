package io.github.lani1234.codereview.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.lani1234.codereview.config.ClaudeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeService {

    private final ClaudeConfig config;
    private final Gson gson = new Gson();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();


    public String reviewCode(String filename, String code) throws IOException {
        log.info("Reviewing code for file: {}", filename);

        validateApiKey();

        String prompt = buildReviewPrompt(filename, code);
        String response = callClaudeApi(prompt);

        log.info("Successfully received review for {}", filename);
        return response;
    }

    private void validateApiKey() {
        if (config.getKey() == null || config.getKey().trim().isEmpty()) {
            throw new IllegalStateException(
                    "ANTHROPIC_API_KEY is not set. Please set it as an environment variable:\n" +
                            "export ANTHROPIC_API_KEY=\"your-key-here\""
            );
        }
    }

    private String buildReviewPrompt(String filename, String code) {
        return String.format("""
                You are an expert Java code reviewer. Review the following code and provide feedback on:
                
                1. CODE QUALITY: Best practices, code organization, naming conventions, readability
                2. BUGS: Potential bugs, logic errors, edge cases not handled
                3. PERFORMANCE: Performance issues, inefficient algorithms, resource usage
                4. SECURITY: Security vulnerabilities, input validation, data exposure
                
                For each issue found:
                - Categorize it (CODE_QUALITY, BUGS, PERFORMANCE, or SECURITY)
                - Indicate severity (HIGH, MEDIUM, LOW)
                - Specify the line number if applicable
                - Provide a clear description and suggestion for improvement
                
                File: %s
                
                Code:
                ```java
                %s
                ```
                
                Provide a structured review with clear sections for each category.
                Be specific and actionable in your feedback.
                """, filename, code);
    }

    private String callClaudeApi(String prompt) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(config.getUrl())
                .addHeader("x-api-key", config.getKey())
                .addHeader("anthropic-version", config.getVersion())
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            // Read response body once and store it
            String responseBodyString = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                log.error("API call failed: {} - {}", response.code(), responseBodyString);

                if (response.code() == 401) {
                    throw new IOException("API authentication failed. Please check your ANTHROPIC_API_KEY.");
                } else if (response.code() == 429) {
                    throw new IOException("Rate limit exceeded. Please try again in a moment.");
                } else if (response.code() == 529) {
                    throw new IOException("Anthropic API is temporarily overloaded. Please wait a minute and try again.");
                } else {
                    throw new IOException("API call failed with status " + response.code());
                }
            }

            if (responseBodyString.isEmpty()) {
                throw new IOException("API returned empty response");
            }

            return extractTextFromResponse(responseBodyString);
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonObject obj = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray content = obj.getAsJsonArray("content");

            if (content == null || content.isEmpty()) {
                throw new IOException("API response missing content");
            }

            return content.get(0).getAsJsonObject().get("text").getAsString();
        } catch (Exception e) {
            log.error("Failed to parse API response: {}", jsonResponse);
            throw new RuntimeException("Failed to parse API response", e);
        }
    }
}