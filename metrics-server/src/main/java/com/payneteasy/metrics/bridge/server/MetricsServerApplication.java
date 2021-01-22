package com.payneteasy.metrics.bridge.server;


import com.payneteasy.http.server.HttpServer;
import com.payneteasy.http.server.log.HttpLoggerJul;
import com.payneteasy.http.server.log.HttpLoggerSystemOut;
import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.server.agents.AgentPollingServiceImpl;
import com.payneteasy.metrics.bridge.server.agents.AgentsHttpRequestHandler;
import com.payneteasy.metrics.bridge.server.agents.TargetRequestSenderAgentImpl;
import com.payneteasy.metrics.bridge.server.prometheus.*;
import com.payneteasy.metrics.bridge.server.queue.TargetQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MetricsServerApplication {

    private static final IHttpLogger LOG = new HttpLoggerJul("com.payneteasy.MetricsServerApplication");

    public static void main(String[] args) {
//        System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");

//        if (System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
//            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
//        }

        IServerConfig            startupConfig = getStartupParameters(IServerConfig.class);
        MetricsServerApplication app           = new MetricsServerApplication();

        try {
            app.start(startupConfig);
        } catch (Exception e) {
            LOG.error("Cannot start web server", e);
            System.exit(1);
        }

    }

    public void start(IServerConfig aConfig) throws IOException {
        TargetQueue<TargetRequest> targetRequestQueue  = new TargetQueue<>("request-queue", 60_000);
        ITargetRequestSender       targetRequestSender = new TargetRequestSenderAgentImpl(targetRequestQueue);
        ITargetFinder              targetFinder        = new TargetFinderImpl(targetRequestSender, aConfig.getPrometheusKeyLifetimeMs());
        AgentPollingServiceImpl    agentPollingService = new AgentPollingServiceImpl(targetRequestQueue, 60_000);
        HttpServer                 agentsWebServer     = createAgentsWebServer(aConfig, agentPollingService);
        HttpServer                 prometheusWebServer = createPrometheusWebServer(aConfig, targetFinder);

        startWebServer(agentsWebServer, "agents-web-server");
        startWebServer(prometheusWebServer, "prometheus-web-server");
    }

    private void startWebServer(HttpServer aServer, String aName) {
        LOG.debug("Starting web server ...", "name", aName);
        Thread thread = new Thread(aServer::acceptSocketAndWait);
        thread.setName(aName);
        thread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(aServer::stop));
    }

    private HttpServer createAgentsWebServer(IServerConfig aConfig, AgentPollingServiceImpl aAgentService) throws IOException {
        return new HttpServer(
                new InetSocketAddress(aConfig.getAgentWebServerPort())
                , new HttpLoggerSystemOut()
                , Executors.newCachedThreadPool()
                , new AgentsHttpRequestHandler(aAgentService)
                , 10_000
        );
    }

    private HttpServer createPrometheusWebServer(IServerConfig aConfig, ITargetFinder targetFinder) throws IOException {
        return new HttpServer(
                new InetSocketAddress(aConfig.getPrometheusWebServerPort())
                , new HttpLoggerJul("com.payneteasy.metrics.bridge.server.prometheus")
                , Executors.newCachedThreadPool()
                , new PrometheusHttpRequestHandler(targetFinder)
                , 10_000
        );
    }

    public void shutdown() {

    }
}
