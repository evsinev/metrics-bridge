package com.payneteasy.metrics.bridge.agent.app.fetch;

import com.payneteasy.http.client.api.HttpHeaders;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;

import static com.payneteasy.metrics.bridge.agent.app.HttpFluentClient.fluentClient;

public class TargetFetchClient implements ITargetFetchClient {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final IHttpClient httpClient;

    public TargetFetchClient(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public TargetFetchResponse fetchFromTarget(TargetFetchRequest aRequest) {



        try {
            HttpResponse response = fluentClient(httpClient)
                    .url              ( "http://localhost:" + aRequest.getTargetPort() + "/metrics")
                    .header           ( "Accept", "application/openmetrics-text; version=0.0.1,text/plain;version=0.0.4;q=0.5,*\\/*;q=0.1")
                    .connectTimeoutMs ( aRequest.getTargetConnectionTimeoutMs())
                    .readTimeoutMs    ( aRequest.getTargetReadTimeoutMs())
                    .doGet();

            return new TargetFetchResponse(
                      response.getStatusCode()
                    , response.getReasonPhrase()
                    , new HttpHeaders(response.getHeaders())
                    , response.getBody()
            );
        } catch (HttpConnectException | HttpReadException | HttpWriteException e) {
            return new TargetFetchResponse(
                      504
                    , e.getMessage()
                    , HttpHeaders.singleHeader("X-Error-Description", e.getMessage())
                    , EMPTY_BYTES
            );
        }
    }
}
