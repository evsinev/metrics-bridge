package com.payneteasy.metrics.bridge.agent.api.polling;

import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;

public interface IAgentPollingService {

     AgentPollingResponse poll(AgentPollingRequest aRequest) throws AgentPollingException;
}
