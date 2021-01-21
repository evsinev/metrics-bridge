package com.payneteasy.metrics.bridge.agent.app.client.polling;

import com.payneteasy.http.client.api.HttpRequest;
import com.payneteasy.http.client.api.HttpRequestParameters;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;

import java.util.concurrent.atomic.AtomicReference;

public class HttpClientMock implements IHttpClient {

    private final AtomicReference<HttpResponse> pendingResponseRef = new AtomicReference<>();
    private final AtomicReference<HttpRequest>  lastRequestRef     = new AtomicReference<>();

    @Override
    public HttpResponse send(HttpRequest aRequest, HttpRequestParameters aParams) throws HttpConnectException, HttpReadException, HttpWriteException {
        HttpResponse response = pendingResponseRef.getAndSet(null);
        lastRequestRef.set(aRequest);
        if (response == null) {
            throw new IllegalStateException("No any pending response. Did you call HttpClientMock.setPendingResponse()?");
        }
        return response;
    }

    public void setPendingResponse(HttpResponse aResponse) {
        HttpResponse old = pendingResponseRef.getAndSet(aResponse);
        if (old != null) {
            throw new IllegalStateException("Pending response is already set");
        }
    }

    public HttpRequest getLastRequest() {
        return lastRequestRef.get();
    }
}
