package com.payneteasy.metrics.bridge.server.agents;

import com.google.gson.Gson;
import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.ProblemDetails;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;

import static com.payneteasy.http.server.api.response.HttpResponseStatusLine.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

public class AgentsHttpRequestHandler implements IHttpRequestHandler {

    private static final HttpResponseMessageBody EMPTY_BODY = new HttpResponseMessageBody(new byte[0]);
    private static final HttpResponseHeaders     NO_HEADERS = new HttpResponseHeaders(emptyList());

    private final Gson                       gson = new Gson();
    private final AgentPollingServiceImpl    agentPollingService;

    public AgentsHttpRequestHandler(AgentPollingServiceImpl aAgentPollingService) {
        agentPollingService = aAgentPollingService;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest aHttpRequest) {
        if(aHttpRequest.getRequestLine().getRequestUri().equals("/health")) {
            return new HttpResponse(HttpResponseStatusLine.OK, NO_HEADERS, EMPTY_BODY);
        }
        try {
            String               remoteIpAddress = aHttpRequest.getRemoteAddress().getAddress().getHostAddress();
            String               json            = aHttpRequest.getBody().asString(UTF_8);
            AgentPollingRequest  agentRequest    = gson.fromJson(json, AgentPollingRequest.class);
            AgentPollingResponse agentResponse   = agentPollingService.poll(remoteIpAddress, agentRequest);
            String               jsonResponse    = gson.toJson(agentResponse);
            return new HttpResponse(OK, NO_HEADERS, new HttpResponseMessageBody(jsonResponse.getBytes(UTF_8)));
        } catch (AgentPollingException e) {
            return createError(e.getType(), e.getMessage(), e);
        } catch (Exception e) {
            return createError(AgentPollingProblemType.UNKNOWN, e.getMessage(), e);
        }
    }

    private HttpResponse createError(AgentPollingProblemType aType, String aMessage, Exception e) {
        ProblemDetails problem = ProblemDetails.builder()
                .type   ( aType.name()     )
                .title  ( aType.getTitle() )
                .detail ( aMessage         )
                .build();
        String json = gson.toJson(problem);
        return new HttpResponse(new HttpResponseStatusLine(aType.getStatus(), aType.getTitle()), NO_HEADERS, new HttpResponseMessageBody(json.getBytes(UTF_8)));
    }
}
