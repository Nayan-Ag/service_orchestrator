package org.example.utils;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowContextutils {
    /**
     * Safely adds a successful API response to the workflowContext.
     *
     * @param context   The shared workflowContext map.
     * @param apiName   The API name key (e.g., get_user_details).
     * @param response  The API's successful response.
     */
    public static void putSuccessResponse(Map<String, Object> context, String apiName, Object response) {
        context.put(apiName, response);
    }

    /**
     * Safely adds a failed API response or error to the workflowContext.
     *
     * @param context   The shared workflowContext map.
     * @param apiName   The API name key (e.g., get_user_details).
     * @param errorMsg  The error message to log.
     */
    public static void putErrorResponse(Map<String, Object> context, String apiName, String errorMsg) {
        context.put(apiName, errorMsg);
    }
}
