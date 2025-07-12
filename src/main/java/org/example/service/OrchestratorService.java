package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.model.*;
import org.example.utils.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service responsible for orchestrating dynamic, multi-step workflows
 * based on a provided slug. It handles recursive execution of workflow steps,
 * manages the execution context, and logs structured request/response payloads.
 */
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final WorkflowUtils workflowUtils;
    private final WorkflowContextutils workflowContextutils;
    private final LoggingUtils loggingUtils;
    private final ApiCaller apiCaller;

    /**
     * Holds the API name currently being invoked (or last invoked).
     */
    private String currentApiName;

    /**
     * Main orchestrator entry point for executing the workflow steps
     * based on the provided slug and input metadata.
     *
     * @param slug    unique task identifier
     * @param headers incoming request headers
     * @param params  query parameters
     * @param body    request body
     * @param req     servlet request (used for logging)
     * @return        final context map containing per-step responses
     * @throws Exception if any workflow step fails
     */
    public Map<String, Object> orchestrate(String slug,
                                           Map<String, Object> headers,
                                           Map<String, Object> params,
                                           Map<String, Object> body,
                                           HttpServletRequest req) throws Exception {

        Map<String, Object> ctx = new LinkedHashMap<>();

        try {
            executeWorkflow(slug, headers, params, body, ctx);
            loggingUtils.logRequestAndResponse(req, 200, slug, headers, params, body, ctx);
            return ctx;
        } catch (Exception ex) {
            loggingUtils.logRequestAndResponse(req, 500, slug, headers, params, body, ctx);
            throw new Exception("Workflow execution failed", ex);
        }
    }

    /**
     * Kicks off the execution of the workflow based on the given slug.
     *
     * @param slug    unique task identifier
     * @param headers headers from client request
     * @param params  query params
     * @param body    request body
     * @param ctx     service context to store step responses
     * @throws Exception if workflow or steps are missing
     */
    private void executeWorkflow(String slug,
                                 Map<String, Object> headers,
                                 Map<String, Object> params,
                                 Map<String, Object> body,
                                 Map<String, Object> ctx) throws Exception {

        Task task = workflowUtils.fetchTaskBySlug(slug);
        Workflow wf = workflowUtils.fetchWorkflowById(task.getWorkflowId());

        if (wf.getSteps() == null || wf.getSteps().isEmpty()) {
            workflowContextutils.putErrorResponse(
                    ctx,
                    "unknown_step",
                    "Workflow contains no steps for slug: " + slug
            );
            throw new RuntimeException("No steps for slug: " + slug);
        }

        invokeStepRecursively(wf, wf.getSteps().get(0), headers, params, body, ctx);
    }

    /**
     * Recursively invokes each step of the workflow and stores the result
     * (or error) in the shared execution context.
     *
     * @param wf      the workflow definition
     * @param step    current step to execute
     * @param headers request headers
     * @param params  request query parameters
     * @param body    request body
     * @param ctx     shared context map for responses
     * @return        true if next step exists and is executed
     * @throws Exception if any API call fails
     */
    private boolean invokeStepRecursively(Workflow wf,
                                          WorkflowSteps step,
                                          Map<String, Object> headers,
                                          Map<String, Object> params,
                                          Map<String, Object> body,
                                          Map<String, Object> ctx) throws Exception {

        currentApiName = step.getApiName();

        try {
            Api apiMeta = workflowUtils.fetchApiByName(currentApiName);
            Map<String, Object> resp = apiCaller.callApi(
                    apiMeta, step, ctx, body, params, headers
            );

            Object raw = resp.get(currentApiName);
            workflowContextutils.putSuccessResponse(ctx, currentApiName, raw);

            String next = (String) resp.getOrDefault("next_task", "break_flow");
            if ("break_flow".equalsIgnoreCase(next)) {
                return false;
            }

            WorkflowSteps nextStep = workflowUtils.fetchStepByApiName(wf, next);
            return invokeStepRecursively(wf, nextStep, headers, params, body, ctx);

        } catch (Exception e) {
            workflowContextutils.putErrorResponse(ctx, currentApiName, e.getMessage());
            throw e;
        }
    }
}
