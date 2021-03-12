package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.http.server.api.response.HttpResponseHeader;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class TargetResponse {

    private final String                     targetRequestId;
    private final int                        status;
    private final String                     reasonPhrase;
    private final List<TargetResponseHeader> headers;
    private final byte[]                     body;

    public List<HttpResponseHeader> getHttpHeaders() {
        List<HttpResponseHeader> ret = new ArrayList<>(headers.size());
        for (TargetResponseHeader header : headers) {
            ret.add(new HttpResponseHeader(header.getName(), header.getValue()));
        }
        return ret;
    }

    @Override
    public String toString() {
        return "TargetResponse{" +
                "targetRequestId='" + targetRequestId + '\'' +
                ", status=" + status +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                ", headers=" + headers +
                ", body.length=" + (body != null ? body.length : "<null>") +
                '}';
    }
}
