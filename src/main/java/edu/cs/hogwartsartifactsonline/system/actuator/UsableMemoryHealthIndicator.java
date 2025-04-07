package edu.cs.hogwartsartifactsonline.system.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UsableMemoryHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        File path = new File("."); // path to compute available disk space
        long diskUsableInBytes = path.getUsableSpace();
        boolean isHealthy = diskUsableInBytes >= 10 * 1024 * 1024; // 10MB
        Status status = isHealthy ? Status.UP : Status.DOWN; // up means there is enough usable memory
        return Health
                .status(status)
                .withDetail("usable memory", diskUsableInBytes)
                .withDetail("threshold", 10 * 1024 * 1024)
                .build();
    }
}
