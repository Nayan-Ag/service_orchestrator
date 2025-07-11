package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.DTO.RequestResponseLogDTO;
import org.example.model.Api;
import org.example.model.Task;
import org.example.model.Workflow;
import org.example.model.WorkflowSteps;
import org.example.utils.DbUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    Map<String, Object> workflowContext = new HashMap<>();

    // Services and Utils;
    private final DbUtils dbUtils;
    private final LoggingService loggingService;
    private final ApiCaller apiCaller;

    // Global Variable with this service Level Scope;
    private Task task;
    private Workflow workflow;
    private Api api;
    private Integer status_code;

    // To fetch the Task from DB based on the SLUG Name in the Request;
    public Map<String, Object> fetchTask(HttpServletRequest request ,String slug, Object headers, Object queryParams, Object requestBody ) throws Exception {
        try {
            task = dbUtils.getTaskBySlug(slug);
            if(task!=null) {
                iterateWorkflow();
            }
            return workflowContext;
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    // To Fetch the Workflow from DB based on the fetched Task and iterate over it to call particular API's
    public void iterateWorkflow() throws Exception {
        try {
            workflow = dbUtils.getWorkflowById(task.get_id());
            if(workflow != null && workflow.getSteps() != null){
                for(WorkflowSteps steps : workflow.getSteps()){
                    ApiCaller(steps);
                }
            }
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    // To fetch particular api from DB and call it;
    public void ApiCaller(WorkflowSteps steps) throws Exception {
        try{
            api = dbUtils.getApiByApiName(steps.getApiName());
            if(api != null){
                Object apiResult = apiCaller.callApi(api , steps, workflowContext);
                workflowContext.put(api.getApiName() , apiResult);
            }
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    // Logging Function to log the Request Response in DB;
    public void Logging(HttpServletRequest request ,String slug, Object headers, Object queryParams, Object requestBody, Object result){
        RequestResponseLogDTO.RequestDataDTO requestDataDTO = new RequestResponseLogDTO.RequestDataDTO();
        requestDataDTO.setUrl(request.getRequestURL().toString());
        requestDataDTO.setSlug(slug);
        requestDataDTO.setMethod(request.getMethod());
        requestDataDTO.setParams(queryParams);
        requestDataDTO.setHeaders(headers);
        requestDataDTO.setBody(requestBody);

        RequestResponseLogDTO.ResponseDataDTO responseDataDTO = new RequestResponseLogDTO.ResponseDataDTO();
        responseDataDTO.setResponse(result);
        responseDataDTO.setStatus_code(status_code);

        RequestResponseLogDTO requestResponseLogDTO = new RequestResponseLogDTO();
        requestResponseLogDTO.setRequest_logs(requestDataDTO);
        requestResponseLogDTO.setResponse_logs(responseDataDTO);

        loggingService.logRequestResponseLog(requestResponseLogDTO);
    }
}
