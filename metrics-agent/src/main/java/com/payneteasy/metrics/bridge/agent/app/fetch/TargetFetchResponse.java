package com.payneteasy.metrics.bridge.agent.app.fetch;

import com.payneteasy.http.client.api.HttpHeaders;
import lombok.Data;

@Data
public class TargetFetchResponse {

    private final int         statusCode;
    private final String      reasonPhrase;
    private final HttpHeaders headers;
    private final byte[]      body;
}
