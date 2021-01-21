package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;

import java.util.Collections;

public class PrometheusHttpRequestHandler implements IHttpRequestHandler {

    /**
     *
     * GET /metrics
     *
     * Headers:
     *   host                                = localhost:8081
     *   x-prometheus-scrape-timeout-seconds = 10.000000
     *   accept-encoding                     = gzip
     *   user-agent                          = Prometheus/2.24.1
     *   accept                              = application/openmetrics-text; version=0.0.1,text/plain;version=0.0.4;q=0.5,*\/*;q=0.1
     */
    @Override
    public HttpResponse handleRequest(HttpRequest httpRequest) {
        return new HttpResponse(HttpResponseStatusLine.OK, new HttpResponseHeaders(Collections.emptyList()), new HttpResponseMessageBody(new byte[0]));
    }
}
