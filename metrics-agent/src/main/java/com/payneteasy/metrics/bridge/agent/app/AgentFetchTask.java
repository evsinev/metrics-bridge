package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.ITargetFetchClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchRequest;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchResponse;
import lombok.Builder;

import static com.payneteasy.metrics.bridge.agent.app.HttpFluentClient.fluentClient;

@Builder
public class AgentFetchTask implements Runnable {

    private final String             fetchId;
    private final ITargetFetchClient targetClient;
    private final TargetFetchRequest targetRequest;
    private final String             serverUploadUrl;
    private final IHttpClient        serverClient;
    private final int                serverConnectionTimeoutMs;
    private final int                serverReadTimeoutMs;

    @Override
    public void run() {
        try {
            TargetFetchResponse response = targetClient.fetchFromTarget(targetRequest);

            fluentClient(serverClient)
                .url(serverUploadUrl + "/" + fetchId)
                .body(response.getBody())
                .headers(response.getHeaders())
                .header("X-Target-Status-Code", response.getStatusCode())
                .header("X-Target-Reason-Phrase", response.getReasonPhrase())
                .doPost();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
