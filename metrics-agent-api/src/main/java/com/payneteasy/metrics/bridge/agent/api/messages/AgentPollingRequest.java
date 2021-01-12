package com.payneteasy.metrics.bridge.agent.api.messages;

import lombok.Data;

@Data
public class AgentPollingRequest {
    private final long   requestId;
    private final String agentId;
    private final int    timeoutMs;
}
