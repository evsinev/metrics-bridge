package com.payneteasy.metrics.bridge.server.prometheus;

import lombok.Data;

@Data
public class TargetResponseHeader {
    private final String name;
    private final String value;
}
