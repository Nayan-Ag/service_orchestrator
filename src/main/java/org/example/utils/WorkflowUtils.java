package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.model.*;
import org.example.repository.*;
import org.springframework.stereotype.Component;

/**
 * Utility component for resolving tasks, workflows, APIs, and workflow steps.
 * Acts as a centralized lookup helper for orchestration operations.
 */
@Component
@RequiredArgsConstructor
public class WorkflowUtils {

    private final TaskRepository taskRepo;
    private final WorkflowRepository wfRepo;
    private final ApiRepository apiRepo;

    /**
     * Retrieves a Task by its unique slug.
     *
     * @param slug the slug identifier for the task
     * @return the matching Task object
     * @throws RuntimeException if slug is null or task not found
     */
    public Task fetchTaskBySlug(String slug) {
        if (slug == null) {
            throw new RuntimeException("Slug is null");
        }
        return taskRepo.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Task not found for slug"));
    }

    /**
     * Retrieves a Workflow using its MongoDB ObjectId.
     *
     * @param workflowId the ObjectId of the workflow
     * @return the corresponding Workflow object
     * @throws RuntimeException if workflowId is null or not found
     */
    public Workflow fetchWorkflowById(ObjectId workflowId) {
        if (workflowId == null) {
            throw new RuntimeException("Workflow id is null");
        }
        return wfRepo.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
    }

    /**
     * Fetches the API metadata for a given API name.
     *
     * @param api the name of the API
     * @return the corresponding Api object
     * @throws RuntimeException if API name is null or not found
     */
    public Api fetchApiByName(String api) {
        if (api == null) {
            throw new RuntimeException("Api Name is null");
        }
        return apiRepo.findByApiName(api)
                .orElseThrow(() -> new RuntimeException("API not found"));
    }

    /**
     * Finds a specific step in the workflow that matches the given API name.
     *
     * @param wf  the Workflow object containing the steps
     * @param api the name of the API to find
     * @return the matching WorkflowSteps object
     * @throws RuntimeException if API name is null or step not found
     */
    public WorkflowSteps fetchStepByApiName(Workflow wf, String api) {
        if (api == null) {
            throw new RuntimeException("API name is null while resolving next step");
        }
        return wf.getSteps().stream()
                .filter(s -> api.equals(s.getApiName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Step not found for API"));
    }
}
