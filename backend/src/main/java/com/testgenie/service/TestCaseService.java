package com.testgenie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgenie.model.RefineRequest;
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

    /**
     * Original generation
     */
    public TestCaseResponse generate(TestCaseRequest request) {
        try {
            if (!request.hasInput()) {
                return TestCaseResponse.error("Please provide a feature description or upload a Jira screenshot.");
            }

            String fullContext = request.buildFullContext();
            String aiResponse = claudeApiService.generateTestCases(fullContext);
            return parseRichResponse(request.getFeatureDescription(), aiResponse, request.getExportFormat());

        } catch (Exception e) {
            return TestCaseResponse.error("Failed to generate test cases: " + e.getMessage());
        }
    }

    /**
     * Refinement — called when user gives feedback
     */
    public TestCaseResponse refine(RefineRequest request) {
        try {
            String aiResponse = claudeApiService.refineTestCases(
                request.getOriginalContext(),
                request.getExistingCasesJson(),
                request.getFeedback(),
                request.getMode()
            );

            return parseRichResponse(
                request.getOriginalContext(),
                aiResponse,
                request.getExportFormat()
            );

        } catch (Exception e) {
            return TestCaseResponse.error("Failed to refine test cases: " + e.getMessage());
        }
    }

    /**
     * Parses the rich AI response into a TestCaseResponse.
     * Used by both generate() and refine() — same output format.
     */
    private TestCaseResponse parseRichResponse(
            String featureDescription,
            String jsonResponse,
            String exportFormat) throws Exception {

        String cleaned = jsonResponse
            .replaceAll("```json", "")
            .replaceAll("```", "")
            .trim();

        JsonNode root = objectMapper.readTree(cleaned);

        String summary = root.path("summary").asText("");

        List<String> riskAreas = new ArrayList<>();
        root.path("riskAreas").forEach(r -> riskAreas.add(r.asText()));

        Map<String, List<Map<String, String>>> categories = new LinkedHashMap<>();
        root.path("categories").fields().forEachRemaining(entry -> {
            String category = entry.getKey();
            List<Map<String, String>> cases = new ArrayList<>();
            entry.getValue().forEach(tc -> {
                Map<String, String> testCase = new LinkedHashMap<>();
                testCase.put("title",    tc.path("title").asText());
                testCase.put("priority", tc.path("priority").asText("P2"));
                testCase.put("given",    tc.path("given").asText());
                testCase.put("when",     tc.path("when").asText());
                testCase.put("then",     tc.path("then").asText());
                cases.add(testCase);
            });
            categories.put(category, cases);
        });

        String formatted = formatOutput(summary, riskAreas, categories, exportFormat);

        return TestCaseResponse.successRich(
            featureDescription != null ? featureDescription : "Jira Ticket Analysis",
            summary,
            riskAreas,
            categories,
            formatted,
            exportFormat
        );
    }

    private String formatOutput(
            String summary,
            List<String> riskAreas,
            Map<String, List<Map<String, String>>> categories,
            String format) {

        return switch (format.toLowerCase()) {
            case "jira"  -> formatAsJira(summary, riskAreas, categories);
            case "csv"   -> formatAsCsv(categories);
            default      -> formatAsMarkdown(summary, riskAreas, categories);
        };
    }

    private String formatAsMarkdown(
            String summary,
            List<String> riskAreas,
            Map<String, List<Map<String, String>>> categories) {

        StringBuilder sb = new StringBuilder();
        sb.append("# Test Cases\n\n");
        sb.append("**Summary:** ").append(summary).append("\n\n");
        sb.append("## ⚠️ Risk Areas\n");
        riskAreas.forEach(r -> sb.append("- ").append(r).append("\n"));
        sb.append("\n");

        categories.forEach((category, cases) -> {
            sb.append("## ").append(category).append("\n\n");
            cases.forEach(tc -> {
                sb.append("### [").append(tc.get("priority")).append("] ")
                  .append(tc.get("title")).append("\n");
                sb.append("- **Given:** ").append(tc.get("given")).append("\n");
                sb.append("- **When:** ").append(tc.get("when")).append("\n");
                sb.append("- **Then:** ").append(tc.get("then")).append("\n\n");
            });
        });

        return sb.toString();
    }

    private String formatAsJira(
            String summary,
            List<String> riskAreas,
            Map<String, List<Map<String, String>>> categories) {

        StringBuilder sb = new StringBuilder();
        sb.append("h1. Test Cases\n\n");
        sb.append("*Summary:* ").append(summary).append("\n\n");
        sb.append("h2. ⚠️ Risk Areas\n");
        riskAreas.forEach(r -> sb.append("* ").append(r).append("\n"));
        sb.append("\n");

        categories.forEach((category, cases) -> {
            sb.append("h2. ").append(category).append("\n\n");
            cases.forEach(tc -> {
                sb.append("h3. [").append(tc.get("priority")).append("] ")
                  .append(tc.get("title")).append("\n");
                sb.append("* *Given:* ").append(tc.get("given")).append("\n");
                sb.append("* *When:* ").append(tc.get("when")).append("\n");
                sb.append("* *Then:* ").append(tc.get("then")).append("\n\n");
            });
        });

        return sb.toString();
    }

    private String formatAsCsv(Map<String, List<Map<String, String>>> categories) {
        StringBuilder sb = new StringBuilder();
        sb.append("Category,Priority,Title,Given,When,Then,Status\n");

        categories.forEach((category, cases) -> {
            cases.forEach(tc -> {
                sb.append(escapeCsv(category)).append(",")
                  .append(escapeCsv(tc.get("priority"))).append(",")
                  .append(escapeCsv(tc.get("title"))).append(",")
                  .append(escapeCsv(tc.get("given"))).append(",")
                  .append(escapeCsv(tc.get("when"))).append(",")
                  .append(escapeCsv(tc.get("then"))).append(",")
                  .append("Not Run\n");
            });
        });

        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
