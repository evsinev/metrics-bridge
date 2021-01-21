package com.payneteasy.metrics.bridge.agent.api.remote;

import com.payneteasy.metrics.bridge.agent.api.remote.exception.AgentRemoteException;
import com.payneteasy.metrics.bridge.agent.api.remote.messages.FetchMetricsResultRequest;
import com.payneteasy.metrics.bridge.agent.api.remote.messages.FetchMetricsResultResponse;

public interface IAgentRemoteService {

    FetchMetricsResultResponse uploadMetricsResult(FetchMetricsResultRequest aRequest) throws AgentRemoteException;
}
