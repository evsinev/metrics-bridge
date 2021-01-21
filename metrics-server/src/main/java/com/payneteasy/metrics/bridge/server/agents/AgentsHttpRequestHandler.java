package com.payneteasy.metrics.bridge.server.agents;

import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;

import java.util.Collections;

public class AgentsHttpRequestHandler implements IHttpRequestHandler {

    @Override
    public HttpResponse handleRequest(HttpRequest httpRequest) {
        return new HttpResponse(HttpResponseStatusLine.OK, new HttpResponseHeaders(Collections.emptyList()), new HttpResponseMessageBody(new byte[0]));
    }
}
