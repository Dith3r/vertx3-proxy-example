package com.dither.handler;

import com.dither.exception.InvalidServiceNameException;
import com.dither.model.ProxyServiceModel;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.http.impl.HeadersAdaptor;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Proxy service handler class
 */
public class ProxyServiceHandlerTest {
    private RoutingContext routingContext;
    private Session session;
    private HttpServerRequest httpServerRequest;
    private HttpClient httpClient;
    private HttpServerResponse httpServerResponse;
    private ProxyServiceModel proxyServiceModel;

    @Before
    public void setUp() throws Exception {
        routingContext = mock(RoutingContext.class);
        session = mock(Session.class);
        httpServerRequest = mock(HttpServerRequest.class);
        httpServerResponse = mock(HttpServerResponse.class);
        httpClient = mock(HttpClient.class);
        proxyServiceModel = mock(ProxyServiceModel.class);

        when(routingContext.session()).thenReturn(session);
        when(routingContext.request()).thenReturn(httpServerRequest);
        when(httpServerRequest.response()).thenReturn(httpServerResponse);
    }

    /**
     * Test handle without proper service group should trigger internal server error
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithoutProperGroup() throws Exception {
        String groupName = "testGroupName";

        when(proxyServiceModel.peekHost(groupName)).thenThrow(new InvalidServiceNameException(groupName));
        when(session.get("group")).thenReturn(groupName);

        ProxyServiceHandler proxyServiceHandler = new ProxyServiceHandler(httpClient, proxyServiceModel);
        proxyServiceHandler.handle(routingContext);

        verify(routingContext, times(1)).fail(500);
    }
}