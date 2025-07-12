package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for holding different segments of API payloads.
 * Used to separate body, query, header, and path params.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayloadBuckets {

    private Map<String, Object> body;
    private Map<String, String> query;
    private Map<String, String> headers;
    private Map<String, String> path;
}
