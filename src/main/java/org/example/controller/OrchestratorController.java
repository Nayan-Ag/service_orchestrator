package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OrchestratorController {

    private final OrchestratorService orchestratorService;

    /**
     * Handles all POST requests to the orchestrator endpoint.
     * Accepts dynamic slug, body, headers, and query parameters.
     *
     * @param request       raw HttpServletRequest for metadata (URL, method)
     * @param slug          identifier for the workflow
     * @param requestBody   payload body (optional)
     * @param headers       incoming headers
     * @param queryParams   incoming query params
     * @return              success or error response
     */
    @PostMapping
    public ResponseEntity<Object> handleRequest(
            HttpServletRequest request,
            @RequestParam String slug,
            @RequestBody(required = false) Map<String, Object> requestBody,
            @RequestHeader Map<String, Object> headers,
            @RequestParam(required = false) Map<String, Object> queryParams
    ) {
        try {
            // Call orchestrator
            Object result = orchestratorService.callHttpRequest(slug, headers, queryParams, requestBody , request);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
