package com.payneteasy.metrics.bridge.integrationtest;

import com.payneteasy.http.client.api.*;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;
import com.payneteasy.http.client.impl.HttpClientImpl;
import com.payneteasy.http.server.log.HttpLoggerSystemOut;
import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.agent.app.IAgentConfig;
import com.payneteasy.metrics.bridge.agent.app.MetricsAgentApplication;
import com.payneteasy.metrics.bridge.server.IServerConfig;
import com.payneteasy.metrics.bridge.server.MetricsServerApplication;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MainIntegrationTest {

    private static final IHttpLogger LOG = new HttpLoggerSystemOut();

    private final IHttpClient           client = new HttpClientImpl();
    private final HttpRequestParameters params = HttpRequestParameters.builder()
            .timeouts(new HttpTimeouts(10_000, 10_000))
            .build();

    @Test
    public void test() throws IOException, InterruptedException, HttpConnectException, HttpReadException, HttpWriteException {
        IServerConfig            serverConfig = getStartupParameters(IServerConfig.class);
        MetricsServerApplication server       = new MetricsServerApplication();
        server.start(serverConfig);

        waitForPort(8080);
        waitForPort(8081);

        try {
            MetricsAgentApplication agent       = new MetricsAgentApplication();
            IAgentConfig            agentConfig = getStartupParameters(IAgentConfig.class);
            agent.start(agentConfig);

            try {
                SampleMetricsServer metrics = new SampleMetricsServer();
                metrics.start(8082);
                waitForPort(8082);

                try {
                    fetchMetrics();
                } finally {
                    metrics.shutdown();
                }
            } finally {
                agent.shutdown();
            }
        } finally {
            server.shutdown();
        }

    }
    
    private void waitForPort(int aPort) throws InterruptedException {
        LOG.debug("Waiting for port ...", "port", aPort);
        HttpRequest request = HttpRequest.builder()
                .method(HttpMethod.GET)
                .url("http://localhost:" + aPort + "/health")
                .build();

        long endTime = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < endTime) {
            try {
                HttpResponse response = client.send(request, params);
                return;
            } catch (Exception e) {
                LOG.error("Cannot make http request to " + aPort + ": " + e.getMessage());
                Thread.sleep(100);
            }
        }

    }

    private void fetchMetrics() throws HttpConnectException, HttpReadException, HttpWriteException {
        HttpHeaders headers = new HttpHeaders(
                Arrays.asList(
                          new HttpHeader("User-Agent", "Prometheus 1.2.3")
                        , new HttpHeader("Accept"    , "application/openmetrics-text")
                        , new HttpHeader("x-prometheus-scrape-timeout-seconds"    , "3.000"))
        );
        HttpRequest request = HttpRequest.builder()
                .method(HttpMethod.GET)
                .url("http://localhost:8081/metrics?host=127.0.0.1&port=8082")
                .headers(headers)
                .build();
        HttpResponse response = client.send(request, params);
        Assert.assertEquals("OK", response.getReasonPhrase());
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("# HELP paynet_executor_flow Executor flow-\n" +
                "# TYPE paynet_executor_flow gauge\n" +
                "flow{unit=\"active_count\",} 0.0\n" +
                "flow{unit=\"completed_task_count\",} 17263.0\n", new String(response.getBody(), StandardCharsets.UTF_8));
    }
}
