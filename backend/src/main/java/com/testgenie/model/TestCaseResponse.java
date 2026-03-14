package com.testgenie.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestCaseResponse {

    private String featureDescription;
    private Map<String, List<String>> testCases;
    private int totalCases;
    private String exportFormat;
    private String formattedOutput;
    private String generatedAt;
    private boolean success;
    private String errorMessage;

    public static TestCaseResponse success(
            String featureDescription,
            Map<String, List<String>> testCases,
            String formattedOutput,
            String exportFormat) {

        TestCaseResponse r = new TestCaseResponse();
        r.featureDescription = featureDescription;
        r.testCases = testCases;
        r.totalCases = testCases.values().stream().mapToInt(List::size).sum();
        r.formattedOutput = formattedOutput;
        r.exportFormat = exportFormat;
        r.generatedAt = Instant.now().toString();
        r.success = true;
        return r;
    }

    public static TestCaseResponse error(String errorMessage) {
        TestCaseResponse r = new TestCaseResponse();
        r.success = false;
        r.errorMessage = errorMessage;
        r.generatedAt = Instant.now().toString();
        return r;
    }

    public String getFeatureDescription() { return featureDescription; }
    public Map<String, List<String>> getTestCases() { return testCases; }
    public int getTotalCases() { return totalCases; }
    public String getExportFormat() { return exportFormat; }
    public String getFormattedOutput() { return formattedOutput; }
    public String getGeneratedAt() { return generatedAt; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
