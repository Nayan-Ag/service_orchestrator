package org.example.DTO;

import lombok.Data;

// Request-Response Log DTO to transfer.
@Data
public class RequestResponseLogDTO {
    private RequestDataDTO request_logs;
    private ResponseDataDTO response_logs;

    @Data
    public static class RequestDataDTO {
        private String url;
        private String slug;
        private String method;
        private Object headers;
        private Object params;
        private Object body;
    }

    @Data
    public static class ResponseDataDTO {
        private int status_code;
        private Object response;
    }
}
