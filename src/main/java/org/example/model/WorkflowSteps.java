package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Map;

@Data
public class WorkflowSteps {
    private String apiName;
    private Map<String, Object> requestValidate;
    private Map<String, ObjectId> responseValidate;
}
