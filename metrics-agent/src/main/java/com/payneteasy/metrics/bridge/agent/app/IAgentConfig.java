package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IAgentConfig {

    @AStartupParameter(name = "METRICS_SERVER_URL", value = "http://localhost:8080")
    String getMetricsServerUrl();

    @AStartupParameter(name = "POLLING_TIMEOUT_MS", value = "60000")
    int getPollingTimeoutMs();

}
