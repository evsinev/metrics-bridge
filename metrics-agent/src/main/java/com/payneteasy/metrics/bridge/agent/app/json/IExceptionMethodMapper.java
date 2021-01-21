package com.payneteasy.metrics.bridge.agent.app.json;

import com.payneteasy.http.client.api.HttpRequest;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;

import java.util.Optional;

public interface IExceptionMethodMapper<X extends Throwable> {

    X onConnectException(HttpConnectException e, HttpRequest request);

    X onReadException(HttpReadException e, HttpRequest request);

    X onWriteException(HttpWriteException e, HttpRequest request);

    Optional<X> checkForError(HttpRequest request, HttpResponse aResponse);
}
