package com.payneteasy.metrics.bridge.server;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IServerConfig {

    @AStartupParameter(name = "AGENTS_PORT", value = "8080")
    int getAgentWebServerPort();

    @AStartupParameter(name = "AGENTS_POLLING_TIMEOUT_MS", value = "60000")
    int getAgentsPollingTimeoutMs();

    @AStartupParameter(name = "PROMETHEUS_PORT", value = "8081")
    int getPrometheusWebServerPort();

    @AStartupParameter(name = "PROMETHEUS_KEY_LIFETIME_MS", value = "20000")
    int getPrometheusKeyLifetimeMs();
}
