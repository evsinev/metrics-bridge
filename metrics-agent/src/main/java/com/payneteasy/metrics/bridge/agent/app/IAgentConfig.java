package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IAgentConfig {

    @AStartupParameter(name = "METRICS_SERVER_URL", value = "http://localhost:8080")
    String getMetricsServerUrl();

    @AStartupParameter(name = "AGENT_ID", value = "agent-01")
    String getAgentId();

    @AStartupParameter(name = "POLLING_TIMEOUT_MS", value = "60000")
    int getPollingTimeoutMs();

    @AStartupParameter(name = "CONNECTION_TIMEOUT_MS", value = "20000")
    int getConnectionTimeoutMs();

}
