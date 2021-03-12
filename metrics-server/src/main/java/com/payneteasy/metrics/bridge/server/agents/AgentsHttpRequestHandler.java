package com.payneteasy.metrics.bridge.server.agents;

import com.google.gson.Gson;
import com.payneteasy.http.server.api.handler.IHttpRequestHandler;
import com.payneteasy.http.server.api.request.HttpRequest;
import com.payneteasy.http.server.api.request.HttpRequestHeader;
import com.payneteasy.http.server.api.request.HttpRequestHeaders;
import com.payneteasy.http.server.api.response.HttpResponse;
import com.payneteasy.http.server.api.response.HttpResponseHeaders;
import com.payneteasy.http.server.api.response.HttpResponseMessageBody;
import com.payneteasy.http.server.api.response.HttpResponseStatusLine;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingException;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.AgentPollingProblemType;
import com.payneteasy.metrics.bridge.agent.api.polling.exception.ProblemDetails;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingRequest;
import com.payneteasy.metrics.bridge.agent.api.polling.messages.AgentPollingResponse;
import com.payneteasy.metrics.bridge.server.prometheus.ITargetFinder;
import com.payneteasy.metrics.bridge.server.prometheus.TargetResponse;
import com.payneteasy.metrics.bridge.server.prometheus.TargetResponseHeader;
import com.payneteasy.metrics.bridge.server.util.Strings;

import java.util.ArrayList;
import java.util.List;

import static com.payneteasy.http.server.api.response.HttpResponseStatusLine.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

public class AgentsHttpRequestHandler implements IHttpRequestHandler {

    private static final HttpResponseMessageBody EMPTY_BODY = new HttpResponseMessageBody(new byte[0]);
    private static final HttpResponseHeaders     NO_HEADERS = new HttpResponseHeaders(emptyList());
    public static final HttpResponse OK_RESPONSE = new HttpResponse(OK, NO_HEADERS, EMPTY_BODY);

    private final Gson                    gson = new Gson();
    private final AgentPollingServiceImpl agentPollingService;
    private final ITargetFinder           targetFinder;
    private final String                  context;

    public AgentsHttpRequestHandler(AgentPollingServiceImpl agentPollingService, ITargetFinder targetFinder, String context) {
        this.agentPollingService = agentPollingService;
        this.targetFinder = targetFinder;
        this.context = context;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest aHttpRequest) {
        String requestUri = removeContext(aHttpRequest.getRequestLine().getRequestUri());

        if(requestUri.startsWith("/health")) {
            return new HttpResponse(HttpResponseStatusLine.OK, NO_HEADERS, EMPTY_BODY);
        }

        if(requestUri.startsWith("/poll")) {
            return processPoll(aHttpRequest);
        }

        if(requestUri.startsWith("/upload")) {
            return processUpload(aHttpRequest);
        }

        return new HttpResponse(new HttpResponseStatusLine(404, "Not found"), NO_HEADERS, EMPTY_BODY);

    }

    String removeContext(String aUri) {
        if(context.equals("/")) {
            return aUri;
        }

        if(context.equals(aUri)) {
            return "/";
        }
        return aUri.substring(context.length());
    }

    private HttpResponse processUpload(HttpRequest aHttpRequest) {
        String             requestUri = aHttpRequest.getRequestLine().getRequestUri();
        int                start      = requestUri.lastIndexOf('/');
        String             fetchId    = requestUri.substring(start + 1);
        HttpRequestHeaders headers    = aHttpRequest.getHeaders();

        TargetResponse targetResponse = TargetResponse.builder()
                .body            ( aHttpRequest.getBody().asBytes())
                .headers         ( convertHeaders(headers))
                .reasonPhrase    ( headers.getString("X-Target-Reason-Phrase"))
                .status          ( headers.getInt("X-Target-Status-Code", 200))
                .targetRequestId ( fetchId)
                .build();
        targetFinder.putTargetResponse(targetResponse);
        return OK_RESPONSE;
    }

    private List<TargetResponseHeader> convertHeaders(HttpRequestHeaders aHttpRequestHeaders) {
        ArrayList<TargetResponseHeader> headers = new ArrayList<>();
        for (HttpRequestHeader header : aHttpRequestHeaders.getOriginalHeaders()) {
            headers.add(new TargetResponseHeader(header.getName(), header.getValue()));
        }
        return headers;
    }

    private HttpResponse processPoll(HttpRequest aHttpRequest) {
        try {
            String               remoteIpAddress = getRemoteAddress(aHttpRequest);
            String               json            = aHttpRequest.getBody().asString(UTF_8);
            AgentPollingRequest  agentRequest    = gson.fromJson(json, AgentPollingRequest.class);
            AgentPollingResponse agentResponse   = agentPollingService.poll(remoteIpAddress, agentRequest);
            String               jsonResponse    = gson.toJson(agentResponse);
            return new HttpResponse(OK, NO_HEADERS, new HttpResponseMessageBody(jsonResponse.getBytes(UTF_8)));
        } catch (AgentPollingException e) {
            return createPollError(e.getType(), e.getMessage(), e);
        } catch (Exception e) {
            return createPollError(AgentPollingProblemType.UNKNOWN, e.getMessage(), e);
        }
    }

    private String getRemoteAddress(HttpRequest aHttpRequest) {
        String realIp = aHttpRequest.getHeaders().getString("X-Real-Ip");
        return Strings.isEmpty(realIp) ? aHttpRequest.getRemoteAddress().getAddress().getHostAddress() : realIp;
    }

    private HttpResponse createPollError(AgentPollingProblemType aType, String aMessage, Exception e) {
        ProblemDetails problem = ProblemDetails.builder()
                .type   ( aType.name()     )
                .title  ( aType.getTitle() )
                .detail ( aMessage         )
                .build();
        String json = gson.toJson(problem);
        return new HttpResponse(new HttpResponseStatusLine(aType.getStatus(), aType.getTitle()), NO_HEADERS, new HttpResponseMessageBody(json.getBytes(UTF_8)));
    }
}
