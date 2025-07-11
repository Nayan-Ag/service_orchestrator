package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.model.*;
import org.example.utils.LoggingUtils;
import org.example.utils.WorkflowUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    /* -----------------------------------------------
     * Dependencies
     * --------------------------------------------- */
    private final WorkflowUtils workflowUtils;
    private final LoggingUtils loggingUtils;
    private final ApiCaller apiCaller;

    /* -----------------------------------------------
     * Shared state for this request
     * --------------------------------------------- */
    private final Map<String, Object> workflowContext = new HashMap<>();

    private Map<String, Object> requestHeaders;
    private Map<String, Object> queryParams;
    private Map<String, Object> requestBody;
    private Workflow workflow;
    private String requestSlug;
    /* ==================================================
     * Entry point: Trigger workflow by SLUG
     * ================================================== */
    public Map<String, Object> callHttpRequest(String slug,
                                   Map<String, Object> headers,
                                   Map<String, Object> params,
                                   Map<String, Object> body,
                                   HttpServletRequest request) throws Exception {
        try{
            this.requestSlug = slug;
            this.requestHeaders = headers;
            this.queryParams = params;
            this.requestBody = body;

            fetchAndExecuteTask();
            loggingUtils.logRequestResponse(request, 200, slug, requestHeaders, queryParams, requestBody, workflowContext);
            return workflowContext;
        }catch(Exception e){
            workflowContext.put("message" , e.getMessage());
            loggingUtils.logRequestResponse(request , 500,slug, requestHeaders, queryParams, requestBody, workflowContext);
            throw new Exception(e.getMessage());
        }
    }

    /* ==================================================
     * Fetch Task by SLUG and trigger associated workflow
     * ================================================== */
    private void fetchAndExecuteTask() throws Exception {
        try {
            Task task = workflowUtils.getTaskBySlug(requestSlug);
            if (task != null) {
                executeWorkflow(task);
            } else {
                throw new RuntimeException("Task not found for slug");
            }
        } catch (Exception e) {
            throw new Exception("Failed to fetch task" );
        }
    }

    /* ==================================================
     * Execute the workflow associated with a task
     * ================================================== */
    private void executeWorkflow(Task task) throws Exception {
        try {
            this.workflow = workflowUtils.getWorkflowById(task.get_id());

            if (workflow != null && workflow.getSteps() != null && !workflow.getSteps().isEmpty()) {
                WorkflowSteps firstStep = workflow.getSteps().get(0);
                callApiStep(firstStep);
            } else {
                throw new RuntimeException("Workflow not found or contains no steps for task ID");
            }
        } catch (Exception e) {
            throw new Exception("Workflow execution failed");
        }
    }

    /* ==================================================
     * Recursively call API steps based on next_step
     * ================================================== */
    private boolean callApiStep(WorkflowSteps step) throws Exception {
        try {
            Api api = workflowUtils.getApiByApiName(step.getApiName());
            if (api == null) throw new RuntimeException("API not found: " + step.getApiName());

            Map<String, Object> response = apiCaller.callApi(
                    api, step, workflowContext, requestBody, queryParams, requestHeaders
            );

            workflowContext.put(api.getApiName(), response);

            String nextApi = (String) response.get("next_step");

            if ("break_flow".equals(nextApi)) {
                return false;
            }

            return callApiStep(workflowUtils.getWorkFlowStepsByApiname(workflow , nextApi));

        } catch (Exception e) {
            throw new Exception("API call failed ");
        }
    }

    /* ==================================================
     * Log request and response into DB
     * ================================================== */
}
