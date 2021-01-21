package com.payneteasy.metrics.bridge.agent.api.polling.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDetails {
    private final String type;
    private final String title;
    private final String detail;
    private final String instance;

}
