package org.example.service;

import org.example.model.Api;
import org.example.model.WorkflowSteps;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiCaller {

    private Api rootApi;
    private WorkflowSteps rootSteps;
    private Map<String, Object> rootWorkflowContext;

    public Object callApi(Api api , WorkflowSteps steps, Map<String, Object> workflowContext) {
        rootApi = api;
        rootSteps = steps;
        rootWorkflowContext = workflowContext;

        if(steps.getRequestValidate() != null){
            if(RequestValidation()){
                makePayload();
            };
        }
        return  "200";
    }

    public boolean RequestValidation(){
        Object requestValidate = rootSteps.getRequestValidate();
        return true;
    }

    public boolean Compare(String comparator){
        switch (comparator){
            case "==" :
                System.out.println("YES");
                return true;

            case "!=" :
                System.out.println("YES");
                return true;

            case "not in":
                System.out.println("YES");
                return true;

            case "in":
                System.out.println("YES");
                return  true;

            default:
                throw  new IllegalArgumentException("Unsupported Comparator");
        }

    }


    public void ResponseValidation() {

    }

    /**
     * Build the payload for one workflow step.
     *
     * @param step          the current WorkflowSteps definition (from DB)
     * @param context       shared context -> every previous API response is stored here
     * @param rawBody       original request body (from the user)
     * @param rawQuery      original query params (from the user)
     * @param rawHeaders    original headers (from the user)
     */
    public void makePayload(WorkflowSteps step,
                                       Map<String, Object> context,
                                       Map<String, Object> rawBody,
                                       Map<String, String> rawQuery,
                                       Map<String, String> rawHeaders) {

        Map<String, Object> body = new HashMap<>();
        Map<String, String> query = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        Map<String, String> path = new HashMap<>();

        /*
         * Each item in step.getPayload() is expected to be a Map with keys like:
         *   name            : "customerId"            // parameter name to send
         *   data_from       : "body" | "query" | "header" | "path" | "response"
         *   data_from_api   : "get_user"              // required only if data_from == response
         *   key             : "cust_id"               // the key to extract from the source map/JSON
         *   send_param_in   : "query" | "header" | "body" | "path"
         */
        for (Map<String, Object> field : rootApi.getPayload()) {

            String targetName = (String) field.get("name");
            String dataFrom = (String) field.get("data_from");
            String sendIn = (String) field.getOrDefault("send_param_in", "body");
            String key = (String) field.get("key");
            String value = (String) field.get("value");

            switch (dataFrom) {
                case "body" -> key = rawBody.get(value);
                case "query" -> key = rawQuery.get(value);
                case "header" -> key = rawHeaders.get(value);
                case "path" -> key = rawBody.get(value); // adjust if you store path vars elsewhere
                case "response" -> {
                    String apiKey = (String) field.get("data_from_api");
                    Object resp = context.get(apiKey);
                    if (resp instanceof Map<?, ?> mapResp) {
                        key = mapResp.get(keyInSource);
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported data_from: " + dataFrom);
            }

            // ---- route to target bucket ------------------------------------
            if (value != null) {
                switch (sendIn) {
                    case "body" -> body.put(targetName, value);
                    case "query" -> query.put(targetName, String.valueOf(value));
                    case "header" -> header.put(targetName, String.valueOf(value));
                    case "path" -> path.put(targetName, String.valueOf(value));
                    default -> throw new IllegalArgumentException("Unsupported send_param_in: " + sendIn);
                }
            } else if (Boolean.TRUE.equals(field.get("required"))) {
                throw new RuntimeException("Missing required payload param: " + targetName);
            }
        }
    }
}
