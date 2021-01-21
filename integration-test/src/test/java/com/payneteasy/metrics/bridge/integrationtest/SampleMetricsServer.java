package com.payneteasy.metrics.bridge.integrationtest;

import com.payneteasy.http.server.HttpServer;
import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;
import com.payneteasy.http.server.impl.response.HttpResponseBuilder;
import com.payneteasy.http.server.log.HttpLoggerSystemOut;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SampleMetricsServer {

    private HttpServer server;

    public void start(int aPort) throws IOException {
        IHttpRequestHandler handler = aRequest -> HttpResponseBuilder.status(HttpResponseStatusLine.OK)
                .addHeader("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
                .body(("# HELP paynet_executor_flow Executor flow-\n" +
                        "# TYPE paynet_executor_flow gauge\n" +
                        "flow{unit=\"active_count\",} 0.0\n" +
                        "flow{unit=\"completed_task_count\",} 17263.0\n").getBytes())
                .build();

        server = new HttpServer(
                new InetSocketAddress(aPort)
                , new HttpLoggerSystemOut()
                , Executors.newCachedThreadPool()
                , handler
                , 10_000
        );

        Thread thread = new Thread(() -> server.acceptSocketAndWait());
        thread.setName("sample-metrics-server");
        thread.start();
    }

    public void shutdown() {
        server.stop();
    }
}
