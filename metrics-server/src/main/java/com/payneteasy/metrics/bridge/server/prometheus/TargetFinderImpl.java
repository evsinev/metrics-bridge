package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.server.log.LogFactory;
import com.payneteasy.metrics.bridge.server.queue.TargetQueue;
import com.payneteasy.metrics.bridge.server.queue.TargetQueueCleanTask;

import javax.annotation.Nonnull;

public class TargetFinderImpl implements ITargetFinder {

    private static final IHttpLogger LOG = LogFactory.createLogger(TargetFinderImpl.class);

    private final ITargetRequestSender        sender;
    private final TargetQueue<TargetResponse> targetQueue;

    public TargetFinderImpl(ITargetRequestSender aSender, int aKeyLifetimeMs) {
        targetQueue = new TargetQueue<>("response-queue", aKeyLifetimeMs);
        sender = aSender;
        new TargetQueueCleanTask(targetQueue).start();
    }

    @Nonnull
    @Override
    public TargetResponse waitForTargetResponse(TargetRequest aRequest) throws PrometheusException {

        // keep in mind that the line below can invoke putTargetResponse() before targetQueue.poll()
        sender.sendTargetRequest(aRequest);

        LOG.debug("Waiting response ...", "request", aRequest);

        TargetResponse response;
        try {
            response = targetQueue.poll(aRequest.getTargetRequestId(), aRequest.getScrapeTimeoutMs() - 500);
        } catch (InterruptedException e) {
            throw new PrometheusException(502, "Interrupted", "Interrupted while waiting for target response from agent", e);
        }

        if (response == null) {
            throw new PrometheusException(502, "No response from agent", "No response from agent for " + aRequest);
        }
        return response;
    }


    @Override
    public void putTargetResponse(TargetResponse aResponse) {
        targetQueue.add(aResponse.getTargetRequestId(), aResponse);
    }
}
