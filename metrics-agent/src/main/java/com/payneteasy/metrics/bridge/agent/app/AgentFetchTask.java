package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.ITargetFetchClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchRequest;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchResponse;
import com.payneteasy.metrics.bridge.agent.app.log.MiniLogger;
import lombok.Builder;

import static com.payneteasy.metrics.bridge.agent.app.HttpFluentClient.fluentClient;
import static com.payneteasy.metrics.bridge.agent.app.log.MiniLoggerFactory.getMiniLogger;

@Builder
public class AgentFetchTask implements Runnable {

    private static final MiniLogger LOG = getMiniLogger( AgentFetchTask.class );

    private final String             fetchId;
    private final ITargetFetchClient targetClient;
    private final TargetFetchRequest targetRequest;
    private final String             serverUploadUrl;
    private final IHttpClient        serverClient;
    private final int                serverConnectionTimeoutMs;
    private final int                serverReadTimeoutMs;
    private final String             bearerHeaderValue;

    @Override
    public void run() {
        try {
            LOG.info("Fetching from target " + targetRequest);
            TargetFetchResponse response = targetClient.fetchFromTarget(targetRequest);

            HttpResponse httpResponse = fluentClient(serverClient)
                    .url(serverUploadUrl + "/" + fetchId)
                    .body(response.getBody())
                    .headers(response.getHeaders())
                    .header("X-Target-Status-Code", response.getStatusCode())
                    .header("X-Target-Reason-Phrase", response.getReasonPhrase())
                    .header("Authorization", bearerHeaderValue )
                    .doPost();

            LOG.info("httpResponse = " + httpResponse);

        } catch (Exception e) {
            LOG.error("Cannot fetch task, fetchId = " + fetchId, e);
        }

    }
}
