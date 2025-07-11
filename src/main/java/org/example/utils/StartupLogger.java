package org.example.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    @Value("${server.port}")
    private String serverPort;

    @PostConstruct
    public void logPort() {
        System.out.println("✅ Server running on port: " + serverPort);
    }
}
