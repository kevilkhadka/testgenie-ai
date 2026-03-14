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

    public String generateTestCases(String fullContext) {
        String prompt = buildPrompt(fullContext);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 4000);

        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content",
            "You are a principal QA engineer with 15+ years of experience in critical systems. "
            + "You specialize in finding edge cases that cause production incidents. "
            + "Always respond with valid JSON only. No markdown, no explanation outside the JSON.");

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

    private String buildPrompt(String fullContext) {
        return "You are analyzing the following feature/ticket to generate critical test cases.\n\n"
            + fullContext + "\n\n"
            + "Generate comprehensive, critical test cases. Return ONLY this exact JSON structure:\n"
            + "{\n"
            + "  \"summary\": \"one sentence summary of what is being tested\",\n"
            + "  \"riskAreas\": [\"risk 1\", \"risk 2\", \"risk 3\"],\n"
            + "  \"categories\": {\n"
            + "    \"Happy Path\": [\n"
            + "      {\n"
            + "        \"title\": \"Verify that...\",\n"
            + "        \"priority\": \"P1\",\n"
            + "        \"given\": \"Given the user is...\",\n"
            + "        \"when\": \"When they...\",\n"
            + "        \"then\": \"Then the system should...\"\n"
            + "      }\n"
            + "    ],\n"
            + "    \"Negative Cases\": [],\n"
            + "    \"Edge Cases\": [],\n"
            + "    \"UI / UX\": [],\n"
            + "    \"Performance\": [],\n"
            + "    \"Security\": []\n"
            + "  }\n"
            + "}\n\n"
            + "Priority rules:\n"
            + "- P1 = critical, blocks release if failed\n"
            + "- P2 = important, should fix before release\n"
            + "- P3 = nice to fix, won't block release\n\n"
            + "Rules:\n"
            + "- At least 5 test cases per category\n"
            + "- P1 cases must be truly critical — data loss, security, payment, auth\n"
            + "- Given/When/Then must be specific and actionable\n"
            + "- Risk areas should highlight the most dangerous failure points\n"
            + "- If Jira ticket content is provided, extract acceptance criteria and base test cases on it\n";
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
