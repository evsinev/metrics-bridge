package com.payneteasy.metrics.bridge.agent.app.fetch;

public interface ITargetFetchClient {

    TargetFetchResponse fetchFromTarget(TargetFetchRequest aTargetRequest);

}
