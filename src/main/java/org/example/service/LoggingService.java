package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.RequestResponseLogDTO;
import org.example.mapper.RequestResponseLogMapper;
import org.example.repository.RequestResponseLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggingService {
    private final RequestResponseLogRepository logRepo;

    // To save the log in DB
    public void logRequestResponseLog(RequestResponseLogDTO log){
        System.out.println(log);
        logRepo.save(RequestResponseLogMapper.toModel(log));
    }
}
