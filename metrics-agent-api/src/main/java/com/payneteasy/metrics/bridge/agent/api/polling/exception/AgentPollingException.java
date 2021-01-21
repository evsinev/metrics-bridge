package com.payneteasy.metrics.bridge.agent.api.polling.exception;

public class AgentPollingException extends Exception {

    private final AgentPollingProblemType type;

    public AgentPollingException(AgentPollingProblemType type, String aDescription, Throwable cause) {
        super(aDescription, cause);
        this.type = type;
    }

    public AgentPollingException(AgentPollingProblemType type, String aDescription) {
        super(aDescription);
        this.type = type;
    }

    public AgentPollingProblemType getType() {
        return type;
    }
}
