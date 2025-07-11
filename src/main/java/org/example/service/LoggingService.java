package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.RequestResponseLog;
import org.example.repository.RequestResponseLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final RequestResponseLogRepository logRepo;

    /**
     * Saves the request-response log to MongoDB.
     *
     * @param log DTO containing request and response data
     */
    public void logRequestResponseLog(RequestResponseLog log) {
        logRepo.save(log);
    }
}
