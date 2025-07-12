package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.example.model.RequestResponseLog;
import org.example.service.LoggingService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility for logging API request and response details to MongoDB.
 * Encapsulates the creation of structured log DTOs.
 */
@Component
public class LoggingUtils {

    private final LoggingService loggingService;

    /**
     * Constructor injection for LoggingService dependency.
     */
    public LoggingUtils(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Logs structured request and response information into MongoDB.
     *
     * @param request         Original HTTP request object
     * @param statusCode      HTTP response status code
     * @param requestSlug     Identifier for the request/task
     * @param requestHeaders  Map of request headers
     * @param queryParams     Map of query parameters
     * @param requestBody     Map representing request body
     * @param workflowContext Map containing API responses (API name → response data)
     */
    public void logRequestAndResponse(HttpServletRequest request,
                                      Integer statusCode,
                                      String requestSlug,
                                      Map<String, Object> requestHeaders,
                                      Map<String, Object> queryParams,
                                      Map<String, Object> requestBody,
                                      Map<String, Object> workflowContext) {

        RequestResponseLog.RequestData requestData = new RequestResponseLog.RequestData();
        requestData.setUrl(request.getRequestURL().toString());
        requestData.setMethod(request.getMethod());
        requestData.setSlug(requestSlug);
        requestData.setHeaders(requestHeaders);
        requestData.setParams(queryParams);
        requestData.setBody(requestBody);

        RequestResponseLog.ResponseData responseData = new RequestResponseLog.ResponseData();
        responseData.setStatus_code(statusCode != null ? statusCode : 200);
        responseData.setResponse(workflowContext);

        RequestResponseLog log = new RequestResponseLog();
        log.setRequest_logs(requestData);
        log.setResponse_logs(responseData);

        loggingService.save(log);
    }
}
