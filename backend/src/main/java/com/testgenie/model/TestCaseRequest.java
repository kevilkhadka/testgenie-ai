package com.testgenie.model;

import jakarta.validation.constraints.Size;

/**
 * TestCaseRequest
 *
 * Updated to support three input modes:
 *   1. Plain text description (original)
 *   2. Jira screenshot (OCR extracts the text)
 *   3. Both — screenshot + extra context typed by the user
 *
 * 💡 LEARNING NOTE:
 *   We removed @NotBlank from featureDescription because now
 *   it's optional — the user might provide an image instead.
 *   We validate the combination in the service layer instead.
 */
public class TestCaseRequest {

    @Size(max = 2000, message = "Description must be under 2000 characters")
    private String featureDescription;

    @Size(max = 1000, message = "Extra context must be under 1000 characters")
    private String extraContext;

    private String exportFormat; // "markdown", "jira", "csv"

    // Text extracted from the uploaded screenshot by OcrService
    // This is set by the controller, not sent by the user directly
    private String ocrExtractedText;

    // Constructors
    public TestCaseRequest() {}

    /**
     * Builds the full context to send to the AI.
     * Combines OCR text + feature description + extra context
     * into one rich prompt input.
     */
    public String buildFullContext() {
        StringBuilder context = new StringBuilder();

        if (ocrExtractedText != null && !ocrExtractedText.isBlank()) {
            context.append("=== JIRA TICKET CONTENT ===\n");
            context.append(ocrExtractedText);
            context.append("\n\n");
        }

        if (featureDescription != null && !featureDescription.isBlank()) {
            context.append("=== FEATURE DESCRIPTION ===\n");
            context.append(featureDescription);
            context.append("\n\n");
        }

        if (extraContext != null && !extraContext.isBlank()) {
            context.append("=== ADDITIONAL CONTEXT ===\n");
            context.append(extraContext);
            context.append("\n\n");
        }

        return context.toString().trim();
    }

    /**
     * Validates that at least one input was provided.
     */
    public boolean hasInput() {
        return (featureDescription != null && !featureDescription.isBlank())
            || (ocrExtractedText != null && !ocrExtractedText.isBlank())
            || (extraContext != null && !extraContext.isBlank());
    }

    // Getters & Setters
    public String getFeatureDescription() { return featureDescription; }
    public void setFeatureDescription(String featureDescription) { this.featureDescription = featureDescription; }

    public String getExtraContext() { return extraContext; }
    public void setExtraContext(String extraContext) { this.extraContext = extraContext; }

    public String getExportFormat() { return exportFormat != null ? exportFormat : "markdown"; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }

    public String getOcrExtractedText() { return ocrExtractedText; }
    public void setOcrExtractedText(String ocrExtractedText) { this.ocrExtractedText = ocrExtractedText; }
}
