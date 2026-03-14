package com.testgenie.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RefineRequest
 *
 * Sent from the frontend when the user gives feedback
 * on existing test cases and wants them refined.
 */
public class RefineRequest {

    // The original feature description or context
    private String originalContext;

    // The existing test cases as a JSON string
    // We send them back so the AI has full context
    @NotBlank(message = "Existing test cases cannot be empty")
    private String existingCasesJson;

    // The user's feedback message
    @NotBlank(message = "Feedback cannot be empty")
    @Size(min = 5, max = 1000, message = "Feedback must be between 5 and 1000 characters")
    private String feedback;

    // "replace" or "append"
    private String mode;

    private String exportFormat;

    // Constructors
    public RefineRequest() {}

    // Getters & Setters
    public String getOriginalContext() { return originalContext; }
    public void setOriginalContext(String originalContext) { this.originalContext = originalContext; }

    public String getExistingCasesJson() { return existingCasesJson; }
    public void setExistingCasesJson(String existingCasesJson) { this.existingCasesJson = existingCasesJson; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getMode() { return mode != null ? mode : "replace"; }
    public void setMode(String mode) { this.mode = mode; }

    public String getExportFormat() { return exportFormat != null ? exportFormat : "markdown"; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }
}
