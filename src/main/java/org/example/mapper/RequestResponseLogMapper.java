package org.example.mapper;

import org.example.DTO.RequestResponseLogDTO;
import org.example.model.RequestResponseLog;

public class RequestResponseLogMapper {
    public static RequestResponseLog toModel(RequestResponseLogDTO dto) {
        RequestResponseLog log = new RequestResponseLog();

        if (dto.getRequest_logs() != null) {
            RequestResponseLog.RequestData req = new RequestResponseLog.RequestData();
            req.setUrl(dto.getRequest_logs().getUrl());
            req.setSlug(dto.getRequest_logs().getSlug());
            req.setMethod(dto.getRequest_logs().getMethod());
            req.setHeaders(dto.getRequest_logs().getHeaders());
            req.setParams(dto.getRequest_logs().getParams());
            req.setBody(dto.getRequest_logs().getBody());
            log.setRequest_logs(req);
        }

        if (dto.getResponse_logs() != null) {
            RequestResponseLog.ResponseData res = new RequestResponseLog.ResponseData();
            res.setStatus_code(dto.getResponse_logs().getStatus_code());
            res.setResponse(dto.getResponse_logs().getResponse());
            log.setResponse_logs(res);
        }
        return log;
    }
}
