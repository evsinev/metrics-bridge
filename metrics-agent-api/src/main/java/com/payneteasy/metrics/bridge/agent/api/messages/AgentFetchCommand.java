package com.payneteasy.metrics.bridge.agent.api.messages;

import lombok.Data;

@Data
public class AgentFetchCommand {

    private final int tcpPort;
    private final int timeoutMs;

}
