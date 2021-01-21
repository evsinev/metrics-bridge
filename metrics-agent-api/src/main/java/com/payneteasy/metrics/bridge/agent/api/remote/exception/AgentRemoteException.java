package com.payneteasy.metrics.bridge.agent.api.remote.exception;

import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;

public class AgentRemoteException extends Exception {

    private final AgentPollingProblemType type;

    public AgentRemoteException(AgentPollingProblemType type, String aDescription, Throwable cause) {
        super(aDescription, cause);
        this.type = type;
    }

    public AgentPollingProblemType getType() {
        return type;
    }
}
