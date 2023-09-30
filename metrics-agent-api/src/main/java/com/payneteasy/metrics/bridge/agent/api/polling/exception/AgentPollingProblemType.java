package com.payneteasy.metrics.bridge.agent.api.polling.exception;

public enum AgentPollingProblemType {

    FORBIDDEN               ( 403, "Wrong auth token"),
    CLIENT_CONNECTION_ERROR (  -1, "Cannot connect to target"),
    CLIENT_READ_ERROR       (  -1, "Cannot read from target"),
    CLIENT_WRITE_ERROR      (  -1, "Cannot write to target"),
    BAD_RESPONSE_CODE       ( 502, "Bad respons code from target"),
    NOT_FOUND               ( 404, "Not found"),
    UNKNOWN                 ( -1 , "Unknown error");

    private final int    status;
    private final String title;


    AgentPollingProblemType(int status, String title) {
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
