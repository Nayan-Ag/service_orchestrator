package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * Represents a single step in a workflow.
 * Each step is typically an API call, with optional request and response validation.
 */
@Data
public class WorkflowSteps {

    /** Name of the API to be called in this workflow step */
    private String apiName;

    /**
     * Request validation rules.
     * These rules define which values must be present in the request before executing this API.
     * Format example:
     * {
     *   "data_from": "body",
     *   "key": "userId",
     *   "value": "123"
     * }
     */
    private Map<String, Object> requestValidate;

    /**
     * Response validation rules.
     * These validate the response from the API.
     * For example:
     * {
     *   "data_from": "response",
     *   "key": "status",
     *   "value": 200
     * }
     */
    private Map<String, ObjectId> responseValidate;

    /**
     * Optional flow definition for success handling.
     * Example: { next: "task/api" , next_step: "taskName / apiName" } for chaining
     */
    private Map<String, Object> onSuccess;

    /**
     * Optional flow definition for failure handling.
     * Example: { next: "task/api" , next_step: "taskName / apiName" } for chaining
     */
    private Map<String, Object> onFail;

}
