package com.payneteasy.metrics.bridge.agent.api.exception;

public enum AgentProblemType {

    FORBIDDEN(403, "Wrong auth token");

    private final int    status;
    private final String title;

    AgentProblemType(int status, String title) {
        this.status = status;
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
