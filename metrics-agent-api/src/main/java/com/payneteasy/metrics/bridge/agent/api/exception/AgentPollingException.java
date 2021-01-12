package com.payneteasy.metrics.bridge.agent.api.exception;

public class AgentPollingException extends Exception {

    private final AgentProblemType type;

    public AgentPollingException(AgentProblemType type, String aDescription, Throwable cause) {
        super(aDescription, cause);
        this.type = type;
    }

    public AgentProblemType getType() {
        return type;
    }
}
