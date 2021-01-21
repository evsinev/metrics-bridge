package com.payneteasy.metrics.bridge.agent.app.client.polling;

import com.google.gson.Gson;
import com.payneteasy.http.client.api.HttpRequestParameters;
import com.payneteasy.http.client.api.HttpTimeouts;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.metrics.bridge.agent.api.polling.IAgentPollingService;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentSettings;
import com.payneteasy.metrics.bridge.agent.app.json.JsonMethodInvoker;

public class AgentPollingClient implements IAgentPollingService {

    private volatile HttpRequestParameters parameters;

    private final JsonMethodInvoker<AgentPollingRequest, AgentPollingResponse, AgentPollingException> methodClient;

    public AgentPollingClient(String aBaseUrl, IHttpClient aHttpClient, String aClientId, int aDefaultConnectionTimeoutMs, int aDefaultReadTimeoutMs, Gson gson) {

        parameters = HttpRequestParameters.builder()
                .timeouts(new HttpTimeouts(aDefaultConnectionTimeoutMs, aDefaultReadTimeoutMs))
                .build();

        methodClient = new JsonMethodInvoker<>(
                aHttpClient
                , gson
                , aBaseUrl + "/poll/" + aClientId
                , AgentPollingResponse.class
                , new AgentPollingExceptionMapper(gson)
        );
    }

    @Override
    public AgentPollingResponse poll(AgentPollingRequest aRequest) throws AgentPollingException {
        AgentPollingResponse response = methodClient.send(aRequest, parameters);
        adjustTimeouts(response.getAgentSettings());
        return response;
    }

    private void adjustTimeouts(AgentSettings agentSettings) {
        if (agentSettings == null) {
            return;
        }

        HttpTimeouts oldTimeouts         = parameters.getTimeouts();
        int          readTimeoutMs       = getParameter(agentSettings.getPollingReadTimeoutMs()      , oldTimeouts.getReadTimeoutMs()   );
        int          connectionTimeoutMs = getParameter(agentSettings.getPollingConnectionTimeoutMs(), oldTimeouts.getConnectTimeoutMs());

        if(oldTimeouts.getReadTimeoutMs() == readTimeoutMs && oldTimeouts.getConnectTimeoutMs() == connectionTimeoutMs) {
            return;
        }

        parameters = HttpRequestParameters.builder().timeouts(new HttpTimeouts(readTimeoutMs, connectionTimeoutMs)).build();
    }

    private int getParameter(int aValue, int aDefaultValue) {
        return aValue > 0 ? aValue : aDefaultValue;
    }
}
