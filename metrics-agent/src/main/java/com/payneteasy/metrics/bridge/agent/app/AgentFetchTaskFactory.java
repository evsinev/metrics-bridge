package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.ITargetFetchClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchRequest;
import lombok.Builder;

@Builder
public class AgentFetchTaskFactory {

    private final String             serverUploadUrl;
    private final IHttpClient        serverClient;
    private final int                serverConnectionTimeoutMs;
    private final int                serverReadTimeoutMs;
    private final ITargetFetchClient targetFetchClient;
    private final String             bearerHeaderValue;

    public AgentFetchTask createFetchTask(String aFetchId, TargetFetchRequest aRequest) {
        return AgentFetchTask.builder()
                .serverClient               ( serverClient              )
                .serverUploadUrl            ( serverUploadUrl           )
                .serverConnectionTimeoutMs  ( serverConnectionTimeoutMs )
                .serverReadTimeoutMs        ( serverReadTimeoutMs       )
                .fetchId                    ( aFetchId                  )
                .targetClient               ( targetFetchClient         )
                .targetRequest              ( aRequest                  )
                .bearerHeaderValue          ( bearerHeaderValue         )
                .build();
    }
}
