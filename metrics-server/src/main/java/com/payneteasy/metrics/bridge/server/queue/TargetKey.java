package com.payneteasy.metrics.bridge.server.queue;

public class TargetKey {
    private final String targetRequestId;
    private final long   endTimeMs;

    public TargetKey(String targetRequestId, long endTimeMs) {
        this.targetRequestId = targetRequestId;
        this.endTimeMs = endTimeMs;
    }

    public String getTargetRequestId() {
        return targetRequestId;
    }

    public long getEndTimeMs() {
        return endTimeMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetKey targetKey = (TargetKey) o;

        return targetRequestId.equals(targetKey.targetRequestId);
    }

    @Override
    public int hashCode() {
        return targetRequestId.hashCode();
    }
}
