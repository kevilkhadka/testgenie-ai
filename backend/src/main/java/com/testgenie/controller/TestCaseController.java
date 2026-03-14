package com.testgenie.controller;

import com.testgenie.model.TestCaseRequest;
import com.testgenie.model.TestCaseResponse;
import com.testgenie.service.OcrService;
import com.testgenie.service.TestCaseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * TestCaseController
 *
 * Updated to handle two modes:
 *   1. POST /api/generate         — plain text (original)
 *   2. POST /api/generate/upload  — image upload + optional text
 *
 * 💡 LEARNING NOTE:
 *   multipart/form-data is the content type used for file uploads.
 *   Unlike JSON, it can carry both text fields AND binary file data
 *   in the same request.
 */
@RestController
@RequestMapping("/api")
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final OcrService ocrService;

    public TestCaseController(TestCaseService testCaseService, OcrService ocrService) {
        this.testCaseService = testCaseService;
        this.ocrService = ocrService;
    }

    /**
     * POST /api/generate
     * Original endpoint — plain text input
     */
    @PostMapping("/generate")
    public ResponseEntity<TestCaseResponse> generate(@RequestBody TestCaseRequest request) {
        TestCaseResponse response = testCaseService.generate(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * POST /api/generate/upload
     * New endpoint — accepts a Jira screenshot + optional text fields
     *
     * Form fields:
     *   - screenshot (file, optional)    — Jira ticket screenshot
     *   - featureDescription (text, optional) — typed description
     *   - extraContext (text, optional)  — additional notes
     *   - exportFormat (text, optional)  — markdown/jira/csv
     */
    @PostMapping(value = "/generate/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestCaseResponse> generateFromUpload(
            @RequestPart(value = "screenshot", required = false) MultipartFile screenshot,
            @RequestParam(value = "featureDescription", required = false) String featureDescription,
            @RequestParam(value = "extraContext", required = false) String extraContext,
            @RequestParam(value = "exportFormat", required = false, defaultValue = "markdown") String exportFormat) {

        // Build the request object
        TestCaseRequest request = new TestCaseRequest();
        request.setFeatureDescription(featureDescription);
        request.setExtraContext(extraContext);
        request.setExportFormat(exportFormat);

        // If a screenshot was uploaded, run OCR on it
        if (screenshot != null && !screenshot.isEmpty()) {
            String extractedText = ocrService.extractText(screenshot);
            request.setOcrExtractedText(extractedText);
        }

        TestCaseResponse response = testCaseService.generate(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("🧪 TestGenie AI is running");
    }
}
