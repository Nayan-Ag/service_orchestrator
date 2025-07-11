package org.example.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Logs important application startup information such as server port.
 */
@Component
public class StartupLogger {

    /**
     * Injected server port value from application.properties or environment.
     */
    @Value("${server.port}")
    private String serverPort;

    /**
     * Method executed after bean construction to log the active server port.
     */
    @PostConstruct
    public void logPort() {
        System.out.println("✅ Server running on port: " + serverPort);
    }
}
