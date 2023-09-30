package com.payneteasy.metrics.bridge.agent.app.json;

import com.google.gson.Gson;
import com.payneteasy.http.client.api.*;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;
import com.payneteasy.metrics.bridge.agent.app.log.MiniLogger;
import com.payneteasy.metrics.bridge.agent.app.log.MiniLoggerFactory;

import java.util.Optional;

import static com.payneteasy.http.client.api.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonMethodInvoker<Q, R, X extends Throwable> {

    private static final MiniLogger LOG = MiniLoggerFactory.getMiniLogger( JsonMethodInvoker.class );

    private final IHttpClient               httpClient;
    private final Gson                      gson;
    private final String                    url;
    private final Class<R>                  responseClass;
    private final IExceptionMethodMapper<X> exceptionConverter;
    private final HttpHeaders               headers;

    public JsonMethodInvoker(
            IHttpClient httpClient
            , Gson gson
            , String url
            , Class<R> responseClass
            , IExceptionMethodMapper<X> exceptionConverter
            , String aAccessToken
    ) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.url = url;
        this.responseClass = responseClass;
        this.exceptionConverter = exceptionConverter;

        headers = HttpHeaders.singleHeader("Authorization", "Bearer " + aAccessToken);
    }

    public R send(Q aRequest, HttpRequestParameters aParams) throws X {
        LOG.info("Sending POST to " + url);

        HttpRequest request = HttpRequest.builder()
                .url     ( url  )
                .method  ( POST )
                .body    ( gson.toJson(aRequest).getBytes(UTF_8))
                .headers ( headers )
                .build();

        HttpResponse response = sendRequest(aParams, request);

        Optional<X> optionalError = exceptionConverter.checkForError(request, response);
        if(optionalError.isPresent()) {
            throw optionalError.get();
        }

        return gson.fromJson(new String(response.getBody(), UTF_8), responseClass);
    }

    private HttpResponse sendRequest(HttpRequestParameters aParams, HttpRequest request) throws X {
        HttpResponse response;
        try {
            response = httpClient.send(request, aParams);
        } catch (HttpConnectException e) {
            throw exceptionConverter.onConnectException(e, request);
        } catch (HttpReadException e) {
            throw exceptionConverter.onReadException(e, request);
        } catch (HttpWriteException e) {
            throw exceptionConverter.onWriteException(e, request);
        }
        return response;
    }
}
