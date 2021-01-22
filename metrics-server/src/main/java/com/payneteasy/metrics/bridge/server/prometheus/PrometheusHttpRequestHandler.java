package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;
import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.server.log.LogFactory;

import java.util.Collections;

import static java.util.UUID.randomUUID;

public class PrometheusHttpRequestHandler implements IHttpRequestHandler {

    private static final IHttpLogger LOG = LogFactory.createLogger(PrometheusHttpRequestHandler.class);

    private static final HttpResponseMessageBody EMPTY_BODY    = new HttpResponseMessageBody(new byte[0]);
    private static final HttpResponseHeaders     EMPTY_HEADERS = new HttpResponseHeaders(Collections.emptyList());

    private final PrometheusRequestParser expositionRequestParser = new PrometheusRequestParser();
    private final ITargetFinder           targetFinder;

    public PrometheusHttpRequestHandler(ITargetFinder targetFinder) {
        this.targetFinder = targetFinder;
    }

    /**
     * GET /metrics
     * <p>
     * Headers:
     * host                                = localhost:8081
     * x-prometheus-scrape-timeout-seconds = 10.000000
     * accept-encoding                     = gzip
     * user-agent                          = Prometheus/2.24.1
     * accept                              = application/openmetrics-text; version=0.0.1,text/plain;version=0.0.4;q=0.5,*\/*;q=0.1
     */
    @Override
    public HttpResponse handleRequest(HttpRequest aRequest) {
        if(aRequest.getRequestLine().getRequestUri().equals("/health")) {
            return new HttpResponse(HttpResponseStatusLine.OK, EMPTY_HEADERS, EMPTY_BODY);
        }
        
        try {
            PrometheusRequest prometheusRequest = expositionRequestParser.parse(aRequest);
            TargetRequest     targetRequest     = createTargetRequest(prometheusRequest);
            TargetResponse    targetResponse    = targetFinder.waitForTargetResponse(targetRequest);

            return new HttpResponse(new HttpResponseStatusLine(targetResponse.getStatus(), targetResponse.getReasonPhrase())
                    , new HttpResponseHeaders(targetResponse.getHttpHeaders())
                    , new HttpResponseMessageBody(targetResponse.getBody()));

        } catch (PrometheusException e) {
            LOG.error("Prometheus error: " + e.getMessage(), e);
            return new HttpResponse(new HttpResponseStatusLine(e.getStatus(), e.getReasonPhrase()), EMPTY_HEADERS, EMPTY_BODY);
        } catch (Exception e) {
            LOG.error("Unknown error", e);
            return new HttpResponse(new HttpResponseStatusLine(500, e.getMessage()), EMPTY_HEADERS, EMPTY_BODY);
        }
    }

    private TargetRequest createTargetRequest(PrometheusRequest aPrometheusRequest) {
        return TargetRequest.builder()
                .targetHost      ( aPrometheusRequest.getTargetHost()      )
                .targetPort      ( aPrometheusRequest.getTargetPort()      )
                .scrapeTimeoutMs ( aPrometheusRequest.getScrapeTimeoutMs() )
                .targetRequestId ( randomUUID().toString()                 )
                .build();
    }
}
