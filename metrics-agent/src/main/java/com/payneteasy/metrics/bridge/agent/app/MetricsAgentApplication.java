package com.payneteasy.metrics.bridge.agent.app;

import com.google.gson.Gson;
import com.payneteasy.http.client.impl.HttpClientImpl;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.agent.app.client.polling.AgentPollingClient;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MetricsAgentApplication {

    public static void main(String[] args) {
        if(System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        }

        IAgentConfig startupConfig = getStartupParameters(IAgentConfig.class);
        new MetricsAgentApplication().start(startupConfig);

    }

    public void start(IAgentConfig aConfig) {
        AgentPollingClient client = new AgentPollingClient(
                aConfig.getMetricsServerUrl()
                , new HttpClientImpl()
                , aConfig.getAgentId()
                , aConfig.getConnectionTimeoutMs()
                , aConfig.getPollingTimeoutMs()
                , new Gson()
        );

        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    AgentPollingResponse response = client.poll(new AgentPollingRequest(System.currentTimeMillis(), aConfig.getAgentId(), aConfig.getPollingTimeoutMs()));
                } catch (AgentPollingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void shutdown() {

    }
}
