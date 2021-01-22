package com.payneteasy.metrics.bridge.server.queue;

public class TargetQueueCleanTask extends Thread {

    private final TargetQueue queue;

    public TargetQueueCleanTask(TargetQueue aQueue) {
        super("target-queue-cleaner");
        setDaemon(true);
        queue = aQueue;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            queue.clean();
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }


}
