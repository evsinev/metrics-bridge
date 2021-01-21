package com.payneteasy.metrics.bridge.agent.api.polling.model;

import lombok.Data;

@Data
public class AgentSettings {
    private final int pollingReadTimeoutMs;
    private final int pollingConnectionTimeoutMs;
}
