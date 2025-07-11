package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.model.RequestResponseLog;
import org.example.service.LoggingService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ++ Utility for logging API request and response details to MongoDB.
 *    Encapsulates the creation of structured log DTOs.
 */
@Component
public class LoggingUtils {

    private final LoggingService loggingService;

    /**
     * ++ Constructor injection for LoggingService dependency.
     */
    public LoggingUtils(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * ++ Logs request and response data into the database.
     *
     * @param request         Original HTTP request (URL, method)
     * @param statusCode      Response status code (e.g., 200 or 500)
     * @param requestSlug     Slug associated with the current task
     * @param requestHeaders  Request headers map
     * @param queryParams     Query parameters map
     * @param requestBody     Request body map
     * @param workflowContext Final API responses stored during workflow
     */
    public void logRequestResponse(HttpServletRequest request,
                                   Integer statusCode,
                                   String requestSlug,
                                   Map<String, Object> requestHeaders,
                                   Map<String, Object> queryParams,
                                   Map<String, Object> requestBody,
                                   Map<String, Object> workflowContext) {

        // ++ Construct request section of log
        RequestResponseLog.RequestData req = new RequestResponseLog.RequestData();
        req.setUrl(request.getRequestURL().toString());
        req.setMethod(request.getMethod());
        req.setSlug(requestSlug);
        req.setHeaders(requestHeaders);
        req.setParams(queryParams);
        req.setBody(requestBody);

        // ++ Construct response section of log
        RequestResponseLog.ResponseData res = new RequestResponseLog.ResponseData();
        res.setResponse(workflowContext);
        res.setStatus_code(statusCode != null ? statusCode : 200);

        // ++ Combine and log
        RequestResponseLog log = new RequestResponseLog();
        log.setRequest_logs(req);
        log.setResponse_logs(res);

        loggingService.logRequestResponseLog(log);
    }
}
