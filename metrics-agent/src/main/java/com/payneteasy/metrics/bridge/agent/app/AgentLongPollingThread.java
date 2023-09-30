package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentFetchMetricsCommand;
import com.payneteasy.metrics.bridge.agent.app.client.polling.AgentPollingClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchRequest;
import com.payneteasy.metrics.bridge.agent.app.log.MiniLogger;

import java.util.concurrent.ExecutorService;

import static com.payneteasy.metrics.bridge.agent.app.log.MiniLoggerFactory.getMiniLogger;

public class AgentLongPollingThread extends Thread {

    private static final MiniLogger LOG = getMiniLogger( AgentLongPollingThread.class );

    private final AgentPollingClient    client;
    private final String                agentId;
    private final int                   longPollingTimeoutMs;
    private final ExecutorService       executorService;
    private final AgentFetchTaskFactory agentFetchTaskFactory;

    public AgentLongPollingThread(AgentPollingClient client, String agentId, int longPollingTimeoutMs, ExecutorService executorService, AgentFetchTaskFactory agentFetchTaskFactory) {
        this.client = client;
        this.agentId = agentId;
        this.longPollingTimeoutMs = longPollingTimeoutMs;
        this.executorService = executorService;
        this.agentFetchTaskFactory = agentFetchTaskFactory;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                AgentPollingResponse     response     = client.poll(new AgentPollingRequest(System.currentTimeMillis(), agentId, longPollingTimeoutMs));

                LOG.info("response = " + response);

                AgentFetchMetricsCommand fetchCommand = response.getFetchCommand();
                LOG.info("fetchCommand = " + fetchCommand);

                if (fetchCommand == null) {
                    continue;
                }

                TargetFetchRequest targetRequest = new TargetFetchRequest(fetchCommand.getTargetTcpPort(), fetchCommand.getTargetConnectionTimeoutMs(), fetchCommand.getTargetReadTimeoutMs());
                LOG.info("targetRequest = " + targetRequest);

                AgentFetchTask fetchTask = agentFetchTaskFactory.createFetchTask(fetchCommand.getFetchId(), targetRequest);
                LOG.info("fetchTask = " + fetchTask);
                executorService.execute(fetchTask);


            } catch (Exception e) {
                LOG.error("Cannot process request", e);
                sleepForError();
            }

        }
    }

    private void sleepForError() {
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            LOG.warn("Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
