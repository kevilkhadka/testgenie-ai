package com.testgenie.service;

import com.fasterxml.jackson.datablind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annoation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClaudeApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model:claude-sonnet-4-20250514}")
    private String model;

    @Value("${claude.api.max-tokens:2000}")
    private int maxTokens;

    public ClaudeApiService(WebClient.Builder webClientBuilder, @Value("${claude.api.url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.objectMapper = objectMapper;
    }

    public String generateTestCases(String featureDescription) {
        String prompt = buildPrompt(featureDescription);

        // Build the JSON request body 
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);

        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);

        // Call the Claude API
        String responseJson = webClient.post()
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json") 
                .bodyValue(requestBody.toString())
                .retrieve()
                ..bodyToMono(String.class)
                .block();

        return extractTextFromResponse(responseJson);
    }

    private String buildPrompt(String featureDescription) {
        return """
            You are a senior QA engineer with 10+ years of experience.

            Given the following feature description, generate comprehensive test cases.

            Feature: %s

            Return ONLY a valid JSON object with this exact structure (no markdown, no explanation):
            {
              "categories": {
                "Happy Path": ["test case 1", "test case 2"],
                "Negative Cases": ["test case 1", "test case 2"],
                "Edge Cases": ["test case 1", "test case 2"],
                "UI / UX": ["test case 1", "test case 2"],
                "Performance": ["test case 1", "test case 2"],
                "Security": ["test case 1", "test case 2"]
              }
            }

            Rules:
            - Each test case must start with "Verify that..."
            - Be specific to the feature described, not generic
            - Include at least 3 test cases per category
            - Detect the domain (auth, payments, file upload, search, etc.)
              and add relevant cases
            """.formatted(featureDescription);
    }

    private String extractTextFromResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode content = root.path("content");

            if (content.isArray() && content.size() > 0) {
                return content.get(0).path("text").asText();
            }

            throw new RuntimeException("Unexpected response from Claude API");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Claude API response: " + e.getMessage(), e);
        }
    }
}

