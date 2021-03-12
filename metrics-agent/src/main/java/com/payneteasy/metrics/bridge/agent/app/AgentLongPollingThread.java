package com.payneteasy.metrics.bridge.agent.app;

import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.agent.api.polling.model.AgentFetchMetricsCommand;
import com.payneteasy.metrics.bridge.agent.app.client.polling.AgentPollingClient;
import com.payneteasy.metrics.bridge.agent.app.fetch.TargetFetchRequest;

import java.util.concurrent.ExecutorService;

public class AgentLongPollingThread extends Thread {

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

                System.out.println("response = " + response);

                AgentFetchMetricsCommand fetchCommand = response.getFetchCommand();
                System.out.println("fetchCommand = " + fetchCommand);

                if (fetchCommand == null) {
                    continue;
                }

                TargetFetchRequest targetRequest = new TargetFetchRequest(fetchCommand.getTargetTcpPort(), fetchCommand.getTargetConnectionTimeoutMs(), fetchCommand.getTargetReadTimeoutMs());
                System.out.println("targetRequest = " + targetRequest);

                AgentFetchTask fetchTask = agentFetchTaskFactory.createFetchTask(fetchCommand.getFetchId(), targetRequest);
                System.out.println("fetchTask = " + fetchTask);
                executorService.execute(fetchTask);


            } catch (Exception e) {
                e.printStackTrace();
                sleepForError();
            }

        }
    }

    private void sleepForError() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
