package com.payneteasy.metrics.bridge.server.http;

import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeader;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.metrics.bridge.server.log.LogFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class HttpGzipHandlerWrapper implements IHttpRequestHandler {

    private static final IHttpLogger LOG = LogFactory.createLogger(HttpGzipHandlerWrapper.class);

    private final IHttpRequestHandler target;

    public HttpGzipHandlerWrapper(IHttpRequestHandler target) {
        this.target = target;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest aRequest) {

        HttpResponse httpResponse  = target.handleRequest(aRequest);

        if (isAcceptGzipEncoding(aRequest)) {
            return wrapWithGzip(httpResponse);
        }

        return httpResponse;
    }

    private boolean isAcceptGzipEncoding(HttpRequest aRequest) {
        String acceptEncodingValue = aRequest.getHeaders().getString("Accept-Encoding");
        return acceptEncodingValue != null && acceptEncodingValue.contains("gzip");
    }

    private HttpResponse wrapWithGzip(HttpResponse httpResponse) {
        boolean isBodyEmpty = isBodyEmpty(httpResponse.getBody());

        HttpResponseMessageBody body    = isBodyEmpty ? httpResponse.getBody()    : toGzipBody(httpResponse.getBody());
        HttpResponseHeaders     headers = isBodyEmpty ? httpResponse.getHeaders() : toGzipHeaders(httpResponse.getHeaders());

        return new HttpResponse(
                  httpResponse.getStatusLine()
                , headers
                , body
        );
    }

    private HttpResponseMessageBody toGzipBody(HttpResponseMessageBody body) {
        if (isBodyEmpty(body)) {
            return body;
        }

        return new HttpResponseMessageBody(compressGzip(body.getBytes()));
    }

    private HttpResponseHeaders toGzipHeaders(HttpResponseHeaders aOriginalHeaders) {
        ArrayList<HttpResponseHeader> headers = new ArrayList<>(aOriginalHeaders.getHeaders().size() + 3);

        for (HttpResponseHeader header : aOriginalHeaders.getHeaders()) {
            if (header.getName().equalsIgnoreCase("Authorization")) {
                continue;
            }
            headers.add(new HttpResponseHeader("X-Origin-" + header.getName(), header.getValue()));
        }

        headers.add(new HttpResponseHeader("Content-Encoding", "gzip"));
        headers.add(new HttpResponseHeader("Content-Type", "text/plain; version=0.0.4; charset=utf-8"));

        return new HttpResponseHeaders(headers);
    }

    private static boolean isBodyEmpty(HttpResponseMessageBody body) {
        return body == null || body.getBytes() == null || body.getBytes().length == 0;
    }

    public static byte[] compressGzip(byte[] aBody) {
        if (aBody == null || aBody.length == 0) {
            return aBody;
        }

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(bytesOut)) {
            gzipOut.write(aBody);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compress gzip body", e);
        }

        return bytesOut.toByteArray();
    }

}
