package com.payneteasy.metrics.bridge.server.prometheus;

import org.junit.Assert;
import org.junit.Test;

import static com.payneteasy.metrics.bridge.server.prometheus.RequestQueryString.parseQueryString;

public class RequestQueryStringTest {

    @Test
    public void parse_uri() throws PrometheusException {
        RequestQueryString params = parseQueryString("/metrics?host=1.2.3.4&port=1234");
        Assert.assertEquals("1.2.3.4", params.getRequiredString("host"));
        Assert.assertEquals("1234", params.getRequiredString("port"));
    }
}