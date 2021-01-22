package com.payneteasy.metrics.bridge.server.queue;

import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.server.log.LogFactory;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TargetQueue<T> {

    private static final IHttpLogger LOG = LogFactory.createLogger(TargetQueue.class);

    private final ConcurrentMap<TargetKey, ArrayBlockingQueue<T>> map;
    private final int                                             keyLifetimeMs;
    private final String queueName;

    public TargetQueue(String aQueueName, int aKeyLifetimeMs) {
        LOG.debug("Creating queue ...", "queue", aQueueName);
        map = new ConcurrentHashMap<>();
        keyLifetimeMs = aKeyLifetimeMs;
        queueName = aQueueName;
    }

    public void clean() {
        Set<TargetKey> keys = map.keySet();
        long now = System.currentTimeMillis();
        for (TargetKey key : keys) {
            if(key.getEndTimeMs() < now) {
                map.remove(key);
            }
        }
    }

    private ArrayBlockingQueue<T> getQueue(String aRequestId) {
        TargetKey key = new TargetKey(aRequestId, System.currentTimeMillis() + keyLifetimeMs);
        return map.computeIfAbsent(key, targetKey -> new ArrayBlockingQueue<>(2));
    }

    public T poll(String aRequestId, int aTimeoutMs) throws InterruptedException {
        LOG.debug("Polling ...", "queue", queueName, "request-id", aRequestId, "timeout-ms", aTimeoutMs);
        ArrayBlockingQueue<T> queue = getQueue(aRequestId);
        T                     result  = queue.poll(aTimeoutMs, TimeUnit.MILLISECONDS);
        LOG.debug("Polled", "queue", queueName, "request-id", aRequestId, "result", result);
        return result;
    }

    public void add(String aRequestId, T aResponse) {
        LOG.debug("Adding ...", "queue", queueName, "request-id", aRequestId, "value", aResponse);
        getQueue(aRequestId).add(aResponse);
    }
}
