package com.payneteasy.metrics.bridge.agent.api.remote.exception;

public enum AgentRemoteProblemType {

    FORBIDDEN(403, "Wrong auth token");

    private final int    status;
    private final String title;

    AgentRemoteProblemType(int status, String title) {
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
