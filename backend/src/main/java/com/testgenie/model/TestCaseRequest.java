package com.testgenie.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TestCaseRequest {

    @NotBlank (message = "Feature description cannot be empty")
    @Size(min=10, max=2000, message = "Description name must be between 10 and 2000 characters")
    private String featureDescription;

    private String exportFormat; // "markdown", "jira", "csv"

    // Constructors
    public TestCaseRequest() {}

    public TestCaseRequest(String featureDescription, String exportFormat) {
        this.featureDescription = featureDescription;
        this.exportFormat = exportFormat;
    }

    // Getters and Setters
    public String getFeatureDescription() {
        return featureDescription;
    }

    public void setFeatureDescription(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public String getExportFormat() {
        return exportFormat != null ? exportFormat : "markdown";
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }
}
