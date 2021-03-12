package com.payneteasy.metrics.bridge.agent.app;

import com.google.gson.Gson;
import com.payneteasy.http.client.impl.HttpClientImpl;
import com.payneteasy.metrics.bridge.agent.app.client.polling.AgentPollingClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchClient;

import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MetricsAgentApplication {

    private AgentLongPollingThread thread;

    public static void main(String[] args) {
        if(System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        }

        IAgentConfig startupConfig = getStartupParameters(IAgentConfig.class);
        new MetricsAgentApplication().start(startupConfig);

    }

    public void start(IAgentConfig aConfig) {
        HttpClientImpl httpClient = new HttpClientImpl();

        AgentPollingClient client = new AgentPollingClient(
                aConfig.getServerBaseUrl()
                , httpClient
                , aConfig.getAgentId()
                , aConfig.getServerReadTimeoutMs()
                , aConfig.getPollingTimeoutMs()
                , new Gson()
        );

        AgentFetchTaskFactory factory = AgentFetchTaskFactory.builder()
                .serverClient(httpClient)
                .serverConnectionTimeoutMs(aConfig.getServerConnectTimeoutMs())
                .serverReadTimeoutMs(aConfig.getServerReadTimeoutMs())
                .serverUploadUrl(aConfig.getServerBaseUrl() + "/upload")
                .targetFetchClient(new TargetFetchClient(httpClient))
                .build();

        thread = new AgentLongPollingThread(
                client
                , aConfig.getAgentId()
                , aConfig.getPollingTimeoutMs()
                , Executors.newCachedThreadPool()
                , factory
        );

        thread.start();
        System.out.println("Thread started");
    }


    public void shutdown() {
        thread.interrupt();
    }
}
