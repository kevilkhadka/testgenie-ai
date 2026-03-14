package com.testgenie.controller;

import com.testgenie.model.TestCaseRequest;
import com.testgenie.model.TestCaseResponse;
import com.testgenie.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    /**
     * POST /api/generate
     * Accepts a feature description, returns AI-generated test cases
     */
    @PostMapping("/generate")
    public ResponseEntity<TestCaseResponse> generate(@Valid @RequestBody TestCaseRequest request) {
        TestCaseResponse response = testCaseService.generate(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/health
     * Simple health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("🧪 TestGenie AI is running");
    }
}
