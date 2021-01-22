package com.payneteasy.metrics.bridge.server.prometheus;

import javax.annotation.Nonnull;

public interface ITargetFinder {

    @Nonnull TargetResponse waitForTargetResponse(TargetRequest aRequest) throws PrometheusException;

    void putTargetResponse(TargetResponse aResponse);

}
