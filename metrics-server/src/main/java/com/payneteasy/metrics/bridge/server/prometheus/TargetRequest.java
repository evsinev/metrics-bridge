package com.payneteasy.metrics.bridge.server.prometheus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TargetRequest {

    private final String targetHost;
    private final int    targetPort;
    private final String targetRequestId;
    private final int    scrapeTimeoutMs;
}
