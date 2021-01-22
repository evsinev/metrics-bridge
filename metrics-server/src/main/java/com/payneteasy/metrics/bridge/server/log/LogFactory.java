package com.payneteasy.metrics.bridge.server.log;

import com.payneteasy.http.server.log.IHttpLogger;

public class LogFactory {

    public static IHttpLogger createLogger(Class<?> aClass) {
        return new HttpLoggerImpl(aClass.getSimpleName());
    }
}
