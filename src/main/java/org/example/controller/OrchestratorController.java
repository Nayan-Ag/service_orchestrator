package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles all incoming orchestrated workflow requests.
 * Delegates processing to {@link OrchestratorService} based on the provided slug.
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OrchestratorController {

    private final OrchestratorService orchestratorService;

    /**
     * Endpoint to execute dynamic API workflows.
     *
     * @param request      the raw servlet request, used primarily for logging
     * @param slug         identifier for the specific task or workflow to execute
     * @param requestBody  optional request body for POST payload
     * @param headers      incoming HTTP headers
     * @param queryParams  query string parameters
     * @return             a response entity containing the result or error structure
     */
    @PostMapping
    public ResponseEntity<Object> handleRequest(
            HttpServletRequest request,
            @RequestParam("slug") String slug,
            @RequestBody(required = false) Map<String, Object> requestBody,
            @RequestHeader Map<String, Object> headers,
            @RequestParam Map<String, Object> queryParams
    ) {
        try {
            requestBody = requestBody != null ? requestBody : Collections.emptyMap();
            headers = headers != null ? headers : Collections.emptyMap();
            queryParams = queryParams != null ? queryParams : Collections.emptyMap();

            Object result = orchestratorService.orchestrate(
                    slug, headers, queryParams, requestBody, request
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Workflow execution failed");
            errorBody.put("message", e.getMessage() != null ? e.getMessage() : "Unknown error");
            return ResponseEntity.status(500).body(errorBody);
        }
    }
}
