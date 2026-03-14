package com.testgenie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClaudeApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model:llama-3.3-70b-versatile}")
    private String model;

    public ClaudeApiService(WebClient.Builder webClientBuilder,
                            @Value("${groq.api.url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateTestCases(String featureDescription) {
        String prompt = buildPrompt(featureDescription);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 2000);

        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a senior QA engineer. Always respond with valid JSON only. No markdown, no explanation.");

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        String responseJson = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("content-type", "application/json")
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractTextFromResponse(responseJson);
    }

    private String buildPrompt(String featureDescription) {
        return "Given the following feature description, generate comprehensive test cases.\n\n"
            + "Feature: " + featureDescription + "\n\n"
            + "Return ONLY a valid JSON object with this exact structure:\n"
            + "{\n"
            + "  \"categories\": {\n"
            + "    \"Happy Path\": [\"test case 1\", \"test case 2\"],\n"
            + "    \"Negative Cases\": [\"test case 1\", \"test case 2\"],\n"
            + "    \"Edge Cases\": [\"test case 1\", \"test case 2\"],\n"
            + "    \"UI / UX\": [\"test case 1\", \"test case 2\"],\n"
            + "    \"Performance\": [\"test case 1\", \"test case 2\"],\n"
            + "    \"Security\": [\"test case 1\", \"test case 2\"]\n"
            + "  }\n"
            + "}\n\n"
            + "Rules:\n"
            + "- Each test case must start with Verify that...\n"
            + "- Be specific to the feature, not generic\n"
            + "- At least 3 test cases per category\n";
    }

    private String extractTextFromResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            return root.path("choices")
                       .get(0)
                       .path("message")
                       .path("content")
                       .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq API response: " + e.getMessage(), e);
        }
    }
}
