package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.RequestResponseLog;
import org.example.repository.RequestResponseLogRepository;
import org.springframework.stereotype.Service;

/**
 * Service responsible for persisting structured
 * request-response logs into MongoDB.
 */
@Service
@RequiredArgsConstructor
public class LoggingService {

    private final RequestResponseLogRepository logRepository;

    /**
     * Persists a request-response log document to MongoDB.
     *
     * @param log the structured request-response data to be saved
     */
    public void save(RequestResponseLog log) {
        logRepository.save(log);
    }
}
