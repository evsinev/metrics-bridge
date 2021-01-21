package com.payneteasy.metrics.bridge.agent.api.polling.model;

import lombok.Data;

@Data
public class AgentFetchMetricsCommand {

    private final String fetchId;
    private final int    targetTcpPort;
    private final int    targetConnectionTimeoutMs;
    private final int    targetReadTimeoutMs;

}
