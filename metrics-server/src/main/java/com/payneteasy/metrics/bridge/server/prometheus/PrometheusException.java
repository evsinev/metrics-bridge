package com.payneteasy.metrics.bridge.server.prometheus;

public class PrometheusException extends Exception {

    private final int    status;
    private final String reasonPhrase;

    public PrometheusException(int status, String aReasonPhrase, String aMessage, Exception e) {
        super(aMessage, e);
        this.status = status;
        reasonPhrase = aReasonPhrase;
    }

    public PrometheusException(int status, String aReasonPhrase, String aMessage) {
        super(aMessage);
        this.status = status;
        reasonPhrase = aReasonPhrase;
    }

    public int getStatus() {
        return status;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
