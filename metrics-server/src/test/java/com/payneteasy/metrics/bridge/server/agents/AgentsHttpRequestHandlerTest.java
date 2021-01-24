package com.payneteasy.metrics.bridge.server.agents;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AgentsHttpRequestHandlerTest {

    @Test
    public void remove_context() {
        assertEquals("/"    , removeContext("/", "/"));
        assertEquals("/"    , removeContext("/test", "/test"));
        assertEquals("/poll", removeContext("/", "/poll"));
        assertEquals("/poll", removeContext("/test", "/test/poll"));

    }

    private String removeContext(String aContext, String aPath) {
        return new AgentsHttpRequestHandler(null, null, aContext).removeContext(aPath);
    }
}