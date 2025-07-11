package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.model.Api;
import org.example.model.Task;
import org.example.model.Workflow;
import org.example.model.WorkflowSteps;
import org.example.repository.ApiRepository;
import org.example.repository.TaskRepository;
import org.example.repository.WorkflowRepository;
import org.springframework.stereotype.Component;

/**
 * Utility class for fetching workflow-related data
 * from MongoDB repositories such as Task, Workflow, and Api.
 */
@Component
@RequiredArgsConstructor
public class WorkflowUtils {

    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final ApiRepository apiRepository;

    /**
     * Fetches a Task by its unique slug.
     *
     * @param slug the unique slug identifier for the task
     * @return the Task object from MongoDB
     * @throws RuntimeException if the slug is invalid or not found
     */
    public Task getTaskBySlug(String slug) {
        return taskRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Invalid Slug: " + slug));
    }

    /**
     * Retrieves the Workflow associated with a given ObjectId.
     *
     * @param _id the ObjectId of the workflow
     * @return the Workflow object
     * @throws RuntimeException if no workflow exists for the given ID
     */
    public Workflow getWorkflowById(ObjectId _id) {
        return workflowRepository.findById(_id)
                .orElseThrow(() -> new RuntimeException("Workflow does not exist for ID: " + _id));
    }

    /**
     * Fetches an API definition using its unique API name.
     *
     * @param apiName the name of the API
     * @return the Api object from MongoDB
     * @throws RuntimeException if the API is not found
     */
    public Api getApiByApiName(String apiName) {
        return apiRepository.findByApiName(apiName)
                .orElseThrow(() -> new RuntimeException("API does not exist: " + apiName));
    }

    /**
     * Finds a specific WorkflowStep from a Workflow using the API name.
     *
     * @param workflow the workflow to search within
     * @param apiName  the name of the API to match
     * @return the matching WorkflowSteps object
     * @throws RuntimeException if the step is not found
     */
    public WorkflowSteps getWorkFlowStepsByApiname(Workflow workflow, String apiName) {
        return workflow.getSteps()
                .stream()
                .filter(step -> apiName.equals(step.getApiName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Workflow step not found for API: " + apiName));
    }
}
