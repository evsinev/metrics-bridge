package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.http.client.api.*;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;

import java.util.ArrayList;
import java.util.List;

public class HttpFluentClient {

    private final IHttpClient      client;
    private final List<HttpHeader> headers = new ArrayList<>();
    private       String           url;
    private       int              connectTimeoutMs;
    private       int              readTimeoutMs;
    private       byte[]           body;

    public static HttpFluentClient fluentClient(IHttpClient aClient) {
        return new HttpFluentClient(aClient);
    }

    public HttpFluentClient(IHttpClient client) {
        this.client = client;
    }

    public HttpFluentClient url(String aUrl) {
        url = aUrl;
        return this;
    }

    public HttpResponse doGet() throws HttpConnectException, HttpReadException, HttpWriteException {
        return doMethod(HttpMethod.GET);
    }

    public HttpResponse doPost() throws HttpConnectException, HttpReadException, HttpWriteException {
        return doMethod(HttpMethod.POST);
    }

    private HttpResponse doMethod(HttpMethod aMethod) throws HttpConnectException, HttpReadException, HttpWriteException {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .headers(new HttpHeaders(headers))
                .method(aMethod)
                .body(body)
                .build();

        HttpRequestParameters params = HttpRequestParameters.builder()
                .timeouts(HttpTimeouts.builder()
                        .connectTimeoutMs(connectTimeoutMs)
                        .readTimeoutMs(readTimeoutMs)
                        .build())
                .build();

        return client.send(request, params);
    }

    public HttpFluentClient header(String aName, String aValue) {
        headers.add(new HttpHeader(aName, aValue));
        return this;
    }

    public HttpFluentClient header(String aName, int aValue) {
        headers.add(new HttpHeader(aName, String.valueOf(aValue)));
        return this;
    }

    public HttpFluentClient connectTimeoutMs(int aConnectTimeoutMs) {
        connectTimeoutMs = aConnectTimeoutMs;
        return this;
    }

    public HttpFluentClient readTimeoutMs(int aReadTimeoutMs) {
        readTimeoutMs = aReadTimeoutMs;
        return this;
    }

    public HttpFluentClient body(byte[] aBody) {
        body = aBody;
        return this;
    }

    public HttpFluentClient headers(List<HttpHeader> aHeaders) {
        headers.addAll(aHeaders);
        return this;
    }

    public HttpFluentClient headers(HttpHeaders aHeaders) {
        headers.addAll(aHeaders.asList());
        return this;
    }
}
