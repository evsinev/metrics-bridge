package com.payneteasy.metrics.bridge.agent.api.messages;

import lombok.Data;

@Data
public class AgentSettings {
    private final int pollingTimeoutMs;
}
