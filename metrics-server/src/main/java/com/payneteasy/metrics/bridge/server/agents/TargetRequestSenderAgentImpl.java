package com.payneteasy.metrics.bridge.server.agents;

import com.payneteasy.metrics.bridge.server.prometheus.ITargetRequestSender;
import com.payneteasy.metrics.bridge.server.prometheus.TargetRequest;
import com.payneteasy.metrics.bridge.server.queue.TargetQueue;

public class TargetRequestSenderAgentImpl implements ITargetRequestSender {

    private final TargetQueue<TargetRequest> targetQueue;

    public TargetRequestSenderAgentImpl(TargetQueue<TargetRequest> targetQueue) {
        this.targetQueue = targetQueue;
    }

    @Override
    public void sendTargetRequest(TargetRequest aRequest) {
        targetQueue.add(aRequest.getTargetHost(), aRequest);
    }
}
