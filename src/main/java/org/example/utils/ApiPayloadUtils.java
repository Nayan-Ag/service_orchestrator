package org.example.utils;

import org.example.model.Api;
import org.example.model.WorkflowSteps;
import org.example.Dto.PayloadBuckets;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for extracting and building API payloads based on metadata.
 */
@Component
public class ApiPayloadUtils {

    /**
     * Extracts a value based on the 'data_from' source (body, query, header, static, or previous API response).
     *
     * @param dataFrom        Source of the data ("body", "query", "header", "static", or "response")
     * @param sourceApi       The API name to fetch response from (if dataFrom is "response")
     * @param val             The key to extract
     * @param reqBody         Incoming request body
     * @param reqQuery        Incoming query parameters
     * @param reqHeaders      Incoming request headers
     * @param workflowContext Context holding previous API responses
     * @return Extracted value or null if not found
     */
    public Object extractValue(String dataFrom,
                               String sourceApi,
                               String val,
                               Map<String, Object> reqBody,
                               Map<String, Object> reqQuery,
                               Map<String, Object> reqHeaders,
                               Map<String, Object> workflowContext) {

        return switch (dataFrom.toLowerCase()) {
            case "body" -> reqBody.get(val);
            case "query" -> reqQuery.get(val);
            case "header" -> reqHeaders.get(val);
            case "static" -> val;
            case "response" -> {
                Object response = workflowContext.get(sourceApi);
                if (response instanceof Map<?, ?> responseMap) {
                    yield responseMap.get(val);
                }
                yield null;
            }
            default -> throw new IllegalStateException("Unsupported data_from") ;
        };
    }

    /**
     * Builds a structured PayloadBuckets object using API metadata and current request/response state.
     *
     * @param apiMeta         API metadata (payload structure)
     * @param stepMeta        Workflow step metadata (unused but reserved for future)
     * @param workflowContext Context with past API responses
     * @param reqBody         Current request body
     * @param reqQuery        Current query parameters
     * @param reqHeaders      Current request headers
     * @return PayloadBuckets with body, query, header, and path data
     */
    public PayloadBuckets buildPayload(Api apiMeta,
                                       WorkflowSteps stepMeta,
                                       Map<String, Object> workflowContext,
                                       Map<String, Object> reqBody,
                                       Map<String, Object> reqQuery,
                                       Map<String, Object> reqHeaders) {

        Map<String, Object> bodyBucket = new HashMap<>();
        Map<String, String> queryBucket = new HashMap<>();
        Map<String, String> headerBucket = new HashMap<>();
        Map<String, String> pathBucket = new HashMap<>();

        for (Map<String, Object> field : apiMeta.getPayload()) {
            String dataFrom = String.valueOf(field.get("data_from")).toLowerCase();
            String sendIn = String.valueOf(field.getOrDefault("send_param_in", "body")).toLowerCase();
            String key = String.valueOf(field.get("key"));
            String val = String.valueOf(field.get("value"));
            String sourceApi = (String) field.get("data_from_api");

            Object extracted = extractValue(dataFrom, sourceApi, val, reqBody, reqQuery, reqHeaders, workflowContext);

            if (extracted != null) {
                switch (sendIn) {
                    case "body" -> bodyBucket.put(key, extracted);
                    case "query" -> queryBucket.put(key, String.valueOf(extracted));
                    case "header" -> headerBucket.put(key, String.valueOf(extracted));
                    case "path" -> pathBucket.put(key, String.valueOf(extracted));
                    default -> throw new IllegalArgumentException("Unsupported send_param_in");
                }
            } else {
                throw new RuntimeException("Missing parameter");
            }
        }

        return new PayloadBuckets(bodyBucket, queryBucket, headerBucket, pathBucket);
    }

    /**
     * Determines the name of the next API to be executed based on the outcome of the current step.
     *
     * @param steps         the current workflow step definition
     * @param lastApiStatus true if the previous API call succeeded, false if it failed
     * @return the name of the next API, or "break_flow" if the flow should end
     */
    public String getNextApi(WorkflowSteps steps, boolean lastApiStatus) throws Exception{
        try{
            Map<String, Object> nextConfig = lastApiStatus ? steps.getOnSuccess() : steps.getOnFail();
            String nextType = (String) nextConfig.get("next");
            System.out.println("mext" + nextConfig);

            return "task".equalsIgnoreCase(nextType)
                    ? "break_flow"
                    : (String) nextConfig.get("next_value");
        }catch (Exception e){
            throw new Exception(e);
        }
    }
}
