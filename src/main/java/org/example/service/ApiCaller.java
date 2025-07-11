package org.example.service;

import org.example.model.Api;
import org.example.model.WorkflowSteps;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiCaller {

    /* =======================
       ——— Instance Variables —
       ======================= */
    private Api               apiMeta;              // API definition from DB
    private WorkflowSteps     stepMeta;             // Step definition (validators, etc.)
    private Map<String, Object> workflowContext;    // Shared context for chained APIs

    private Map<String, Object> reqHeaders;         // Incoming request headers
    private Map<String, Object> reqQuery;           // Incoming query params
    private Map<String, Object> reqBody;            // Incoming body (already parsed)

    /* ==================================================
       ——— Public entry point called by Orchestrator ———
       ================================================== */
    public Map<String, Object> callApi(Api api,
                          WorkflowSteps step,
                          Map<String, Object> context,
                          Map<String, Object> headers,
                          Map<String, Object> query,
                          Map<String, Object> body) {

        // Bind instance state
        this.apiMeta         = api;
        this.stepMeta        = step;
        this.workflowContext = context;
        this.reqHeaders      = headers != null ? headers : new HashMap<>();
        this.reqQuery        = query   != null ? query   : new HashMap<>();
        this.reqBody         = body    != null ? body    : new HashMap<>();

        if (step.getRequestValidate() != null && !requestValidation()) {
            throw new RuntimeException("Request validation failed for API: " + api.getApiName());
        }
        PayloadBuckets payload = buildPayload();
        Object apiResult = makeHttpCall(api.getApiUrl() , api.getApiMethod() , payload);
        workflowContext.put(api.getApiName(), apiResult);

        /* —— 4. Optional response validation —— */
        if (step.getResponseValidate() != null && !responseValidation(apiResult)) {
            throw new RuntimeException("Response validation failed for API: " + api.getApiName());
        }

        Map<String , Object> result = new HashMap<>();
        result.put(api.getApiName() , apiResult);
        return result;
    }

    /* ==================================================
       ——— Validation Stubs (extend later) ———
       ================================================== */
    private boolean requestValidation() {
        // TODO: implement your validation logic against stepMeta.getRequestValidate()
        return true;
    }

    private boolean responseValidation(Object response) {
        // TODO: implement your validation logic against stepMeta.getResponseValidate()
        return true;
    }

    /* ==================================================
       ——— Comparator utility for validations ———
       ================================================== */
    private boolean compare(Object left, String comparator, Object right) {
        return switch (comparator) {
            case "==", "equals"      -> left == null ? right == null : left.equals(right);
            case "!=", "<>", "not equals" -> left == null ? right != null : !left.equals(right);
            case "in"                -> right instanceof List<?> lst && lst.contains(left);
            case "not in"            -> right instanceof List<?> lst && !lst.contains(left);
            default -> throw new IllegalArgumentException("Unsupported comparator: " + comparator);
        };
    }

    /* ==================================================
       ——— Build outbound payload per step definition ———
       ================================================== */
    private PayloadBuckets buildPayload() {

        Map<String, Object> bodyBucket   = new HashMap<>();
        Map<String, String> queryBucket  = new HashMap<>();
        Map<String, String> headerBucket = new HashMap<>();
        Map<String, String> pathBucket   = new HashMap<>();

        for (Map<String, Object> field : apiMeta.getPayload()) {
            String dataFrom = ((String) field.get("data_from")).toLowerCase();
            String sendIn   = ((String) field.getOrDefault("send_param_in", "body")).toLowerCase();

            String sourceApi = (String) field.get("data_from_api");  // only for data_from == response
            String key       = (String) field.get("key");            // key to extract from source
            String Val = (String) field.get("value");          // for data_from == static

            /* ---- Extract the value from the correct place ---- */
            Object extracted = extractValue(dataFrom, sourceApi, Val);

            /* ---- Route to the correct bucket ---- */
            if (extracted != null) {
                switch (sendIn) {
                    case "body"   -> bodyBucket.put(key, extracted);
                    case "query"  -> queryBucket.put(key, String.valueOf(extracted));
                    case "header" -> headerBucket.put(key, String.valueOf(extracted));
                    case "path"   -> pathBucket.put(key, String.valueOf(extracted));
                    default -> throw new IllegalArgumentException("Unsupported send_param_in: " + sendIn);
                }
            } else {
                throw new RuntimeException("Missing required param '" + key + "' for API " + apiMeta.getApiName());
            }
        }
        return new PayloadBuckets(bodyBucket, queryBucket, headerBucket, pathBucket);
    }

    /* ==================================================
       ——— Helper: extract value based on data_from ———
       ================================================== */
    private Object extractValue(String dataFrom,
                                String sourceApi,
                                String Val) {

        return switch (dataFrom) {
            case "body"    -> reqBody.get(Val);
            case "query"   -> reqQuery.get(Val);
            case "header"  -> reqHeaders.get(Val);
            case "static"  -> Val;
            case "response" -> {
                Object resp = workflowContext.get(sourceApi);              // previous API response
                if (resp instanceof Map<?,?> map) {
                    yield map.get(Val);
                }
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected data_from: " + dataFrom);
        };
    }

    private Object makeHttpCall(String url, String method, PayloadBuckets payload) {

        RestTemplate restTemplate = new RestTemplate();

        // Build headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(payload.headers());

        /* ── 1. Inject path variables ─────────────────────────── */
        // Example: url = "https://api.example.com/user/{userId}/order/{orderId}"
        String resolvedUrl = url;

        // Append all path segments directly to the base URL
        if (!payload.path().isEmpty()) {
            StringBuilder pathBuilder = new StringBuilder();
            for (String pathValue : payload.path().values()) {
                String encoded = UriUtils.encodePath(pathValue, StandardCharsets.UTF_8);
                pathBuilder.append("/").append(encoded);
            }

            // Final URL = base + path
            resolvedUrl = resolvedUrl.endsWith("/")
                    ? resolvedUrl.substring(0, resolvedUrl.length() - 1)
                    : resolvedUrl;
            resolvedUrl += pathBuilder.toString();
        }

        // Attach query params to URL
        String finalUrl = resolvedUrl;
        if (!payload.query().isEmpty()) {
            StringBuilder queryString = new StringBuilder("?");
            payload.query().forEach((k, v) -> queryString.append(k).append("=").append(v).append("&"));
            finalUrl += queryString.substring(0, queryString.length() - 1);
        }

        // Create request entity
        HttpEntity<?> requestEntity = switch (method.toUpperCase()) {
            case "GET", "DELETE" -> new HttpEntity<>(httpHeaders); // No body
            case "POST", "PUT", "PATCH" -> new HttpEntity<>(payload.body(), httpHeaders);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };

        // Choose correct HTTP method
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        if (httpMethod == null) {
            throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }

        // Make the call
        ResponseEntity<Map> response = restTemplate.exchange(
                finalUrl,
                httpMethod,
                requestEntity,
                Map.class
        );

        return response.getBody(); // You can also log status, headers if needed
    }

    /* ==================================================
       ——— Small DTO for payload buckets ———
       ================================================== */
    private record PayloadBuckets(Map<String, Object> body,
                                  Map<String, String> query,
                                  Map<String, String> headers,
                                  Map<String, String> path) {}
}
