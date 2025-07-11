package org.example.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "req_res_log")
public class RequestResponseLog {
    @Id
    private ObjectId _id;
    private RequestData request_logs;
    private ResponseData response_logs;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime created_at;

    @Data
    public static class RequestData {
        private String url;
        private String slug;
        private String method;
        private Object headers;
        private Object params;
        private Object body;
    }

    @Data
    public static class ResponseData {
        private int status_code;
        private Object response;
    }
}