package com.testgenie.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestCaseResponse {

    private String featureDescription;
    private Map<String, List<String>> testCases;
    private int testCases;
    private String exportFormat;
    private String formattedOutput;
    private boolean success;
    private String errorMessage;

    // Success factory method
    public static TestCaseResponse success(
        String featureDescription, 
        Map<String, List<String>> testCases, 
        int testCases, 
        String exportFormat, 
        String formattedOutput) {
        TestCaseResponse response = new TestCaseResponse();
        response.featureDescription = featureDescription;
        response.testCases = testCases;
        response.testCases = testCases.values().stream().flatMap(List::size).sum();
        response.exportFormat = exportFormat;
        response.formattedOutput = formattedOutput;
        response.generatedAt = Instant.now().toString();
        response.success = true;
        return response;
    }
}

// Error factory method
public static TestCaseResponse error(String errorMessage) {
    TestCaseResponse response = new TestCaseResponse();
    response.errorMessage = errorMessage;
    response.success = false;
    response.generatedAt = Instant.now().toString();
    return response;
}

//Getters
public String getFeatureDescription() {
    return featureDescription;
}

public Map<String, List<String>> getTestCases() {
    return testCases;
}

public int getTotalTestCases() {
    return totalTestCases;
}

public String getExportFormat() {
    return exportFormat;
}

public String getFormattedOutput() {
    return formattedOutput;
}

public boolean isSuccess() {
    return success;
}

public String getErrorMessage() {
    return errorMessage;
}

public String getGeneratedAt() {
    return generatedAt;
}
