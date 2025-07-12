package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Dto.PayloadBuckets;
import org.example.model.Api;
import org.example.model.WorkflowSteps;
import org.example.utils.ApiPayloadUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Handles dynamic HTTP calls based on workflow step and API metadata.
 * Constructs full URL, headers, query params, and request body based on payload logic.
 */
@Service
@RequiredArgsConstructor
public class ApiCaller {

    private final ApiPayloadUtils apiPayloadUtils;

    private static final Set<HttpStatus> VALID_STATUS_CODES = Set.of(
            HttpStatus.OK, HttpStatus.NO_CONTENT
    );

    /**
     * Executes an external API call based on provided workflow metadata.
     *
     * @param apiMeta     metadata of the API to call
     * @param stepMeta    current workflow step configuration
     * @param workflowCtx current context of the overall workflow
     * @param reqBody     request body (if any)
     * @param reqQuery    query parameters
     * @param reqHeaders  request headers
     * @return            map containing response body and next step
     * @throws Exception  in case of any failure during API execution
     */
    public Map<String, Object> callApi(Api apiMeta,
                                       WorkflowSteps stepMeta,
                                       Map<String, Object> workflowCtx,
                                       Map<String, Object> reqBody,
                                       Map<String, Object> reqQuery,
                                       Map<String, Object> reqHeaders) throws Exception {

        try {
            PayloadBuckets payload = apiPayloadUtils.buildPayload(
                    apiMeta,
                    stepMeta,
                    workflowCtx,
                    Optional.ofNullable(reqBody).orElseGet(HashMap::new),
                    Optional.ofNullable(reqQuery).orElseGet(HashMap::new),
                    Optional.ofNullable(reqHeaders).orElseGet(HashMap::new)
            );

            ResponseEntity<Map> apiResp = makeHttpCall(
                    apiMeta.getApiUrl(),
                    apiMeta.getApiMethod(),
                    payload
            );

            boolean apiStatus = VALID_STATUS_CODES.contains(apiResp.getStatusCode());
            String nextTask = apiPayloadUtils.getNextApi(stepMeta, apiStatus);

            return Map.of(
                    apiMeta.getApiName(), apiResp.getBody(),
                    "next_task", nextTask
            );
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Prepares and sends an HTTP request with dynamic headers, path params, and body.
     *
     * @param baseUrl  base URL of the external API
     * @param method   HTTP method (GET, POST, etc.)
     * @param payload  extracted payload including path/query/body/headers
     * @return         response entity containing the response body
     * @throws Exception on any connection or format error
     */
    private ResponseEntity<Map> makeHttpCall(String baseUrl,
                                             String method,
                                             PayloadBuckets payload) throws Exception {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAll(payload.getHeaders());

            StringBuilder url = new StringBuilder(baseUrl.replaceAll("/+$", ""));
            payload.getPath().values().forEach(value ->
                    url.append('/').append(UriUtils.encodePath(value, StandardCharsets.UTF_8))
            );

            if (!payload.getQuery().isEmpty()) {
                url.append('?');
                payload.getQuery().forEach((key, value) ->
                        url.append(key).append('=')
                                .append(UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8))
                                .append('&')
                );
                url.setLength(url.length() - 1);
            }

            String finalUrl = url.toString();

            HttpEntity<?> entity = switch (method.toUpperCase()) {
                case "GET", "DELETE"        -> new HttpEntity<>(headers);
                case "POST", "PUT", "PATCH" -> new HttpEntity<>(payload.getBody(), headers);
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            };

            return restTemplate.exchange(
                    finalUrl,
                    HttpMethod.valueOf(method.toUpperCase()),
                    entity,
                    Map.class
            );
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
}
