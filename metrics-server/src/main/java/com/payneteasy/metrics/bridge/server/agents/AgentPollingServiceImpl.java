package com.payneteasy.metrics.bridge.server.agents;

import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentFetchMetricsCommand;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentSettings;
import com.payneteasy.metrics.bridge.server.prometheus.TargetRequest;
import com.payneteasy.metrics.bridge.server.queue.TargetQueue;

public class AgentPollingServiceImpl {

    private final TargetQueue<TargetRequest> queue;
    private final int                        agentPollTimeMs;

    public AgentPollingServiceImpl(TargetQueue<TargetRequest> queue, int agentPollTimeMs) {
        this.queue = queue;
        this.agentPollTimeMs = agentPollTimeMs;
    }

    public AgentPollingResponse poll(String aRemoteIpAddress, AgentPollingRequest aRequest) throws AgentPollingException {
        try {
            TargetRequest targetRequest = queue.poll(aRemoteIpAddress, 60_000);

            AgentFetchMetricsCommand fetchCommand;
            if(targetRequest != null) {
                fetchCommand = new AgentFetchMetricsCommand(targetRequest.getTargetRequestId(), targetRequest.getTargetPort(), targetRequest.getScrapeTimeoutMs(), targetRequest.getScrapeTimeoutMs());
            } else {
                fetchCommand = null;
            }
            return new AgentPollingResponse(
                    fetchCommand, new AgentSettings(agentPollTimeMs, agentPollTimeMs)
            );
        } catch (InterruptedException e) {
            throw new AgentPollingException(AgentPollingProblemType.UNKNOWN, "Interrupted", e);
        }

    }
}
