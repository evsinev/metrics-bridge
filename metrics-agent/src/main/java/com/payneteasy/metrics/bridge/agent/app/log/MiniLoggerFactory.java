package com.payneteasy.metrics.bridge.agent.app.log;

public class MiniLoggerFactory {

    public static MiniLogger getMiniLogger(Class<?> aClass) {
        return new MiniLogger(aClass.getSimpleName());
    }
}
