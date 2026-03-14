package com.testgenie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgenie.model.TestCaseRequest;
import com.testgenie.model.TestCaseResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestCaseService {

    private final ClaudeApiService claudeApiService;
    private final ObjectMapper objectMapper;

    public TestCaseService(ClaudeApiService claudeApiService) {
        this.claudeApiService = claudeApiService;
        this.objectMapper = new ObjectMapper();
    }

    public TestCaseResponse generate(TestCaseRequest request) {
        try {
            // 1. Call Claude API
            String aiResponse = claudeApiService.generateTestCases(request.getFeatureDescription());

            // 2. Parse the JSON response into a map
            Map<String, List<String>> testCases = parseTestCases(aiResponse);

            // 3. Format output based on requested format
            String formatted = formatOutput(
                request.getFeatureDescription(),
                testCases,
                request.getExportFormat()
            );

            return TestCaseResponse.success(
                request.getFeatureDescription(),
                testCases,
                formatted,
                request.getExportFormat()
            );

        } catch (Exception e) {
            return TestCaseResponse.error("Failed to generate test cases: " + e.getMessage());
        }
    }

    private Map<String, List<String>> parseTestCases(String jsonResponse) throws Exception {
        // Claude sometimes wraps response in markdown code blocks — strip them
        String cleaned = jsonResponse
            .replaceAll("```json", "")
            .replaceAll("```", "")
            .trim();

        JsonNode root = objectMapper.readTree(cleaned);
        JsonNode categories = root.path("categories");

        // LinkedHashMap preserves the order of categories
        Map<String, List<String>> result = new LinkedHashMap<>();

        categories.fields().forEachRemaining(entry -> {
            String category = entry.getKey();
            List<String> cases = new ArrayList<>();
            entry.getValue().forEach(tc -> cases.add(tc.asText()));
            result.put(category, cases);
        });

        return result;
    }

    private String formatOutput(String feature, Map<String, List<String>> testCases, String format) {
        return switch (format.toLowerCase()) {
            case "jira"  -> formatAsJira(feature, testCases);
            case "csv"   -> formatAsCsv(feature, testCases);
            default      -> formatAsMarkdown(feature, testCases);
        };
    }

    private String formatAsMarkdown(String feature, Map<String, List<String>> testCases) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Test Cases: ").append(feature).append("\n\n");

        testCases.forEach((category, cases) -> {
            sb.append("## ").append(category).append("\n");
            cases.forEach(tc -> sb.append("- [ ] ").append(tc).append("\n"));
            sb.append("\n");
        });

        return sb.toString();
    }

    private String formatAsJira(String feature, Map<String, List<String>> testCases) {
        StringBuilder sb = new StringBuilder();
        sb.append("h1. Test Cases: ").append(feature).append("\n\n");

        testCases.forEach((category, cases) -> {
            sb.append("h2. ").append(category).append("\n");
            cases.forEach(tc -> sb.append("* ").append(tc).append("\n"));
            sb.append("\n");
        });

        return sb.toString();
    }

    private String formatAsCsv(String feature, Map<String, List<String>> testCases) {
        StringBuilder sb = new StringBuilder();
        sb.append("Category,Test Case,Status\n");

        testCases.forEach((category, cases) -> {
            cases.forEach(tc -> {
                String escapedCategory = "\"" + category.replace("\"", "\"\"") + "\"";
                String escapedTc = "\"" + tc.replace("\"", "\"\"") + "\"";
                sb.append(escapedCategory).append(",").append(escapedTc).append(",Not Run\n");
            });
        });

        return sb.toString();
    }
}
