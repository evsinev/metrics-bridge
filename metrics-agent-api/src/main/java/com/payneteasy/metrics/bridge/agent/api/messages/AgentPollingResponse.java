package com.payneteasy.metrics.bridge.agent.api.messages;

import lombok.Data;

@Data
public class AgentPollingResponse {

    private final AgentFetchCommand fetchCommand;
    private final AgentSettings     agentSettings;

}
