package com.payneteasy.metrics.bridge.server.prometheus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrometheusRequest {

    private final int    scrapeTimeoutMs;
    private final String targetHost;
    private final int    targetPort;

}
