package com.payneteasy.metrics.bridge.server.log;

import com.payneteasy.http.server.log.IHttpLogger;
import com.payneteasy.http.server.log.LogMessageFormatter;

public class HttpLoggerImpl implements IHttpLogger {

    private final LogMessageFormatter formatter = new LogMessageFormatter();

    private final String name;

    public HttpLoggerImpl(String aName) {
        name = aName;
    }

    public void debug(String aMessage, Object... aKeysValues) {
        System.out.println(getPrefix() + this.formatter.format(aMessage, aKeysValues));
    }

    private String getPrefix() {
        return Thread.currentThread().getName() + " " + name + " ";
    }

    public void error(String aMessage) {
        System.err.println(getPrefix() + this.formatter.format(aMessage, new Object[0]));
    }

    public void error(String aMessage, Exception aException) {
        System.err.println(getPrefix() + this.formatter.formatException(aMessage, aException));
    }
}
