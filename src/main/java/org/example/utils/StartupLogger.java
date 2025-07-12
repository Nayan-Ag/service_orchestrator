package org.example.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility component to log important startup information such as server port.
 */
@Component
public class StartupLogger {

    /**
     * Injected server port from application properties.
     */
    @Value("${server.port}")
    private String serverPort;

    /**
     * Logs the active server port on application startup.
     */
    @PostConstruct
    public void logServerPort() {
        System.out.println("[App] ✅ Server started on port: " + serverPort);
    }
}
