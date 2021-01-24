package com.payneteasy.metrics.bridge.agent.app.fetch;

import lombok.Data;

@Data
public class TargetFetchRequest {

    private final int targetPort;
    private final int targetConnectionTimeoutMs;
    private final int targetReadTimeoutMs;
}
