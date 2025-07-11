package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Represents an API definition stored in the MongoDB "api" collection.
 * Each API includes method, URL, payload structure, and behavior rules.
 */
@Data
@Document(collection = "api")
public class Api {

    /** Unique identifier for the API document */
    @Id
    private ObjectId _id;

    /** Unique name for the API (used for step linking) */
    private String apiName;

    /** HTTP method (GET, POST, PUT, etc.) */
    private String apiMethod;

    /** Endpoint URL, may contain path variables like /user/{id} */
    private String apiUrl;

    /**
     * Payload definition list.
     * Each entry defines:
     * - data_from: source of the value (body, header, query, response, static)
     * - data_from_api: if data_from: response, to fetch the key from any previous called api.
     * - send_param_in: where to send it (body, header, query, path)
     * - key: key name to send
     * - value: "123" in case of static and "response.data.123" in case of response
     */
    private List<Map<String, Object>> payload;

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
