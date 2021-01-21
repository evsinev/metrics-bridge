package com.payneteasy.metrics.bridge.agent.app.client.polling;

import com.google.gson.Gson;
import com.payneteasy.http.client.api.HttpRequest;
import com.payneteasy.http.client.api.HttpRequestParameters;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;
import com.payneteasy.metrics.bridge.agent.api.polling.IAgentPollingService;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.*;

public class AgentPollingClientTest {

    @Test
    public void poll_ok() throws AgentPollingException {
        HttpClientMock httpClient = new HttpClientMock();
        Gson           gson       = new Gson();

        IAgentPollingService pollingService = new AgentPollingClient(
                "http://localhost:1234/metrics-bridge", httpClient, "agent-01", 10, 11, gson
        );

        httpClient.setPendingResponse(new HttpResponse(200, "OK", Collections.emptyList(), "{'fetchCommand' : {'fetchId' : 'fetch-123', 'targetTcpPort' : 8080}}".getBytes(StandardCharsets.UTF_8)));

        AgentPollingResponse response = pollingService.poll(new AgentPollingRequest(1L, "agent-01", 9));

        assertNotNull("FetchCommand section must be set", response.getFetchCommand());
        assertEquals("fetch-123", response.getFetchCommand().getFetchId());
        assertEquals(8080, response.getFetchCommand().getTargetTcpPort());

        assertEquals("http://localhost:1234/metrics-bridge/poll/agent-01", httpClient.getLastRequest().getUrl());
    }

    @Test
    public void poll_500() {
        HttpClientMock httpClient = new HttpClientMock();
        Gson           gson       = new Gson();

        IAgentPollingService pollingService = new AgentPollingClient(
                "http://localhost:1234/metrics-bridge", httpClient, "agent-01", 10, 11, gson
        );

        httpClient.setPendingResponse(new HttpResponse(500, "Server Error", Collections.emptyList(), "{'type' : 'FORBIDDEN'}".getBytes(StandardCharsets.UTF_8)));

        try {
            pollingService.poll(new AgentPollingRequest(1L, "agent-01", 9));
            Assert.fail("Exception should be thrown");
        } catch (AgentPollingException e) {
            Assert.assertEquals(AgentPollingProblemType.FORBIDDEN, e.getType());
        }

    }


}