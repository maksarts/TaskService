package ru.maksarts.taskservice.model;

import java.util.stream.Stream;

public enum TaskStatus {
    OPEN (100), PROGRESS (200), WAITING (300), RESOLVED (400);

    private int status;

    private TaskStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static TaskStatus of(int status) {
        return Stream.of(TaskStatus.values())
                .filter(s -> s.getStatus() == status)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid taskStatusValue: %s", status)));
    }

    public static TaskStatus of(String status) {
        return Stream.of(TaskStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid status: %s", status)));
    }
}
