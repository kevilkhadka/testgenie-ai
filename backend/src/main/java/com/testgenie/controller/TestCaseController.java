package com.testgenie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgenie.model.RefineRequest;
import com.testgenie.model.TestCaseRequest;
import com.testgenie.model.TestCaseResponse;
import com.testgenie.service.OcrService;
import com.testgenie.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final OcrService ocrService;
    private final ObjectMapper objectMapper;

    public TestCaseController(TestCaseService testCaseService, OcrService ocrService) {
        this.testCaseService = testCaseService;
        this.ocrService = ocrService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * POST /api/generate
     * Plain text input
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
     * Jira screenshot + optional text
     */
    @PostMapping(value = "/generate/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestCaseResponse> generateFromUpload(
            @RequestPart(value = "screenshot", required = false) MultipartFile screenshot,
            @RequestParam(value = "featureDescription", required = false) String featureDescription,
            @RequestParam(value = "extraContext", required = false) String extraContext,
            @RequestParam(value = "exportFormat", required = false, defaultValue = "markdown") String exportFormat) {

        TestCaseRequest request = new TestCaseRequest();
        request.setFeatureDescription(featureDescription);
        request.setExtraContext(extraContext);
        request.setExportFormat(exportFormat);

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
     * POST /api/refine
     * Refine existing test cases based on user feedback
     *
     * Request body:
     * {
     *   "originalContext": "checkout flow with credit card",
     *   "existingCasesJson": "{...}",
     *   "feedback": "add more mobile scenarios",
     *   "mode": "append",
     *   "exportFormat": "markdown"
     * }
     */
    @PostMapping("/refine")
    public ResponseEntity<TestCaseResponse> refine(@Valid @RequestBody RefineRequest request) {
        TestCaseResponse response = testCaseService.refine(request);
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
