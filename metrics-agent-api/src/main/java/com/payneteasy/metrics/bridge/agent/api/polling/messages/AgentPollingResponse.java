package com.payneteasy.metrics.bridge.agent.api.polling.messages;

import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentFetchMetricsCommand;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentSettings;
import lombok.Data;

@Data
public class AgentPollingResponse {

    private final AgentFetchMetricsCommand fetchCommand;
    private final AgentSettings            agentSettings;

}
