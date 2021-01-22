package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.request.HttpRequestHeaders;
import com.payneteasy.metrics.bridge.server.util.Strings;

import static com.payneteasy.metrics.bridge.server.prometheus.RequestQueryString.parseQueryString;

public class PrometheusRequestParser {

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
    PrometheusRequest parse(HttpRequest aRequest) throws PrometheusException {

        HttpRequestHeaders headers         = aRequest.getHeaders();
        int                scrapeTimeoutMs = parseSeconds(headers.getRequiredString("x-prometheus-scrape-timeout-seconds"));
        String             userAgent       = headers.getRequiredString("User-Agent");
        String             accept          = headers.getRequiredString("Accept");
        RequestQueryString queryString     = parseQueryString(aRequest.getRequestLine().getRequestUri());

        if (!userAgent.startsWith("Prometheus")) {
            throw new PrometheusException(402, "Wrong user agent", "User agent should start with 'Prometheus' but was " + userAgent);
        }

        if(!accept.startsWith("application/openmetrics-text")) {
            throw new PrometheusException(403, "Wrong accept header", "Accept header should start with 'application/openmetrics-text' but was " + accept);
        }

        return PrometheusRequest.builder()
                .scrapeTimeoutMs ( scrapeTimeoutMs                             )
                .targetHost      ( queryString.getRequiredString("host") )
                .targetPort      ( queryString.getRequiredInt("port")    )
                .build();
    }

    private int parseSeconds(String aSeconds) throws PrometheusException {
        if(Strings.isEmpty(aSeconds)) {
            return 10_000;
        }
        try {
            double seconds = Double.parseDouble(aSeconds);
            return (int) (Math.round(seconds * 1000));
        } catch (NumberFormatException e) {
            throw new PrometheusException(402, "Bad value for x-prometheus-scrape-timeout-seconds", "x-prometheus-scrape-timeout-seconds is wrong : " + aSeconds, e);
        }
    }
}
