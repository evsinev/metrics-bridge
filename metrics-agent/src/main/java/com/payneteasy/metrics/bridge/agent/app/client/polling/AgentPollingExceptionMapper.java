package com.payneteasy.metrics.bridge.agent.app.client.polling;

import com.google.gson.Gson;
import com.payneteasy.http.client.api.HttpRequest;
import com.payneteasy.http.client.api.HttpResponse;
import com.payneteasy.http.client.api.exceptions.HttpConnectException;
import com.payneteasy.http.client.api.exceptions.HttpReadException;
import com.payneteasy.http.client.api.exceptions.HttpWriteException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.ProblemDetails;
import com.payneteasy.metrics.bridge.agent.app.json.IExceptionMethodMapper;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType.*;

public class AgentPollingExceptionMapper implements IExceptionMethodMapper<AgentPollingException> {

    private final Gson gson;

    public AgentPollingExceptionMapper(Gson gson) {
        this.gson = gson;
    }

    @Override
    public AgentPollingException onConnectException(HttpConnectException e, HttpRequest request) {
        return new AgentPollingException(CLIENT_CONNECTION_ERROR, "Connection error to " + request.getUrl(), e);
    }

    @Override
    public AgentPollingException onReadException(HttpReadException e, HttpRequest request) {
        return new AgentPollingException(CLIENT_READ_ERROR, "Read error from " + request.getUrl(), e);
    }

    @Override
    public AgentPollingException onWriteException(HttpWriteException e, HttpRequest request) {
        return new AgentPollingException(CLIENT_WRITE_ERROR, "Write error to " + request.getUrl(), e);
    }

    @Override
    public Optional<AgentPollingException> checkForError(HttpRequest request, HttpResponse aResponse) {
        if(aResponse.getStatusCode() == 200) {
            return Optional.empty();
        }

        ProblemDetails problem = gson.fromJson(new String(aResponse.getBody(), StandardCharsets.UTF_8), ProblemDetails.class);

        AgentPollingProblemType type;
        try {
            type = AgentPollingProblemType.valueOf(problem.getType());
        } catch (Exception e) {
            type = UNKNOWN;
        }

        return Optional.of(new AgentPollingException(type, problem.getDetail()));


    }
}
