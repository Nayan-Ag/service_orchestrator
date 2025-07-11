package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.LoggingService;
import org.example.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OrchestratorController {

    // Services
    private final OrchestratorService orchestratorService ;

    // Post Request, received;
    @PostMapping
    public ResponseEntity<Object> handleRequest(
            HttpServletRequest request,
            @RequestParam String slug,
            @RequestBody(required = false) Map<String, Object> requestBody,
            @RequestHeader Map<String, String> headers,
            @RequestParam(required = false) Map<String, String> queryParams
    ) {
        try {
            orchestratorService.fetchTask(request, slug , headers, queryParams , requestBody);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
