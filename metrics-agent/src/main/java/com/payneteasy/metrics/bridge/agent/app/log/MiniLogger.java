package com.payneteasy.metrics.bridge.agent.app.log;

import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

public class MiniLogger {

    private final String name;

    public MiniLogger(String aName) {
        name = aName;
    }

    public void info(String aMessage) {
        log("INFO ", aMessage);
    }

    public void error(String aMessage, Exception e) {
        log("ERROR", aMessage);
        //noinspection CallToPrintStackTrace
        e.printStackTrace();
    }

    public void warn(String aMessage) {
        log("WARN ", aMessage);
    }

    private void log(String aLevel, String aMessage) {
        System.out.println(
                RFC_1123_DATE_TIME.format(ZonedDateTime.now())
                + " "
                + aLevel
                + " ["
                + Thread.currentThread().getName()
                + "] "
                + name
                + " "
                + aMessage
        );
    }
}
