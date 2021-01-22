package com.payneteasy.metrics.bridge.agent.app.json;

import com.google.gson.Gson;
import com.payneteasy.http.client.api.HttpRequest;
import com.payneteasy.http.client.api.HttpRequestParameters;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.IHttpClient;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static com.payneteasy.http.client.api.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonMethodInvoker<Q, R, X extends Throwable> {

    private final IHttpClient               httpClient;
    private final Gson                      gson;
    private final String                    url;
    private final Class<R>                  responseClass;
    private final IExceptionMethodMapper<X> exceptionConverter;

    public JsonMethodInvoker(IHttpClient httpClient, Gson gson, String url, Class<R> responseClass, IExceptionMethodMapper<X> exceptionConverter) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.url = url;
        this.responseClass = responseClass;
        this.exceptionConverter = exceptionConverter;
    }

    public R send(Q aRequest, HttpRequestParameters aParams) throws X {
        HttpRequest request = HttpRequest.builder()
                .url    ( url  )
                .method ( POST )
                .body   ( gson.toJson(aRequest).getBytes(UTF_8))
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
