package com.payneteasy.metrics.bridge.agent.api;

import com.payneteasy.metrics.bridge.agent.api.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.messages.AgentPollingResponse;

public interface IAgentPollingService {

     AgentPollingResponse poll(AgentPollingRequest aRequest) throws AgentPollingException;
}
