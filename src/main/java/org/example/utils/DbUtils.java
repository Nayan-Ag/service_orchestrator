package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.model.Api;
import org.example.model.Task;
import org.example.model.Workflow;
import org.example.repository.ApiRepository;
import org.example.repository.TaskRepository;
import org.example.repository.WorkflowRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbUtils {

    // Mongo Repositories to Fetch Required Details from DB
    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final ApiRepository apiRepository;

    // To Fetch the Task based on the SLUG in request.
    public Task getTaskBySlug( String slug){
        return taskRepository.findBySlug(slug).
                orElseThrow(() -> new RuntimeException("Invalid Slug"));
    }

    // To Get workflow based on the fetched task id.
    public Workflow getWorkflowById(ObjectId _id){
        return workflowRepository.findById(_id).
                orElseThrow(() -> new RuntimeException("Workflow Not exsits"));
    }

    //  To get each and Every Api based on the workflow Steps.
    public Api getApiByApiName(String apiName){
        return apiRepository.findByApiName(apiName).
                orElseThrow(() -> new RuntimeException("Api Not Exist"));
    }
}
