package com.testgenie.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestCaseResponse {

    private String featureDescription;
    private String summary;
    private List<String> riskAreas;
    private Map<String, List<Map<String, String>>> testCases;
    private int totalCases;
    private String exportFormat;
    private String formattedOutput;
    private String generatedAt;
    private boolean success;
    private String errorMessage;

    // --- Rich success factory method (new) ---
    public static TestCaseResponse successRich(
            String featureDescription,
            String summary,
            List<String> riskAreas,
            Map<String, List<Map<String, String>>> testCases,
            String formattedOutput,
            String exportFormat) {

        TestCaseResponse r = new TestCaseResponse();
        r.featureDescription = featureDescription;
        r.summary = summary;
        r.riskAreas = riskAreas;
        r.testCases = testCases;
        r.totalCases = testCases.values().stream().mapToInt(List::size).sum();
        r.formattedOutput = formattedOutput;
        r.exportFormat = exportFormat;
        r.generatedAt = Instant.now().toString();
        r.success = true;
        return r;
    }

    // --- Error factory method ---
    public static TestCaseResponse error(String errorMessage) {
        TestCaseResponse r = new TestCaseResponse();
        r.success = false;
        r.errorMessage = errorMessage;
        r.generatedAt = Instant.now().toString();
        return r;
    }

    // --- Getters ---
    public String getFeatureDescription() { return featureDescription; }
    public String getSummary() { return summary; }
    public List<String> getRiskAreas() { return riskAreas; }
    public Map<String, List<Map<String, String>>> getTestCases() { return testCases; }
    public int getTotalCases() { return totalCases; }
    public String getExportFormat() { return exportFormat; }
    public String getFormattedOutput() { return formattedOutput; }
    public String getGeneratedAt() { return generatedAt; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
