package com.dither.handler;

import com.dither.dal.MongoUserDAL;
import com.dither.dal.UserDAL;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test find user handler
 */
public class FindUserHandlerTest extends HandlerTest {

    private UserDAL userDAL;
    private RoutingContext routingContext;
    private Session session;
    private HttpServerRequest httpServerRequest;

    @Before
    public void setUp() {
        userDAL = mock(MongoUserDAL.class);
        routingContext = mock(RoutingContext.class);
        session = mock(Session.class);
        httpServerRequest = mock(HttpServerRequest.class);

        when(routingContext.session()).thenReturn(session);
        when(routingContext.request()).thenReturn(httpServerRequest);
    }

    /**
     * Test handler function when in session has already defined group value should trigger only next on context
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithGroupInSession() throws Exception {
        when(session.get("group")).thenReturn("testGroup");
        FindUserHandler findUserHandler = new FindUserHandler(userDAL);
        findUserHandler.handle(routingContext);
        // should never call user dal for data
        verify(userDAL, never()).findUser(any(), any());
        // trigger next handler
        verify(routingContext).next();
    }

    /**
     * Test handler without out id in request params should return bad request
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithoutId() throws Exception {
        when(session.get("group")).thenReturn(null);
        HttpServerRequest httpServerRequest = mock(HttpServerRequest.class);

        FindUserHandler findUserHandler = new FindUserHandler(userDAL);
        findUserHandler.handle(routingContext);

        verify(routingContext).fail(400);
        verify(routingContext, never()).next();
    }

    /**
     * Test handler with failed user dal query should return server internal error state
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithFailedAsyncResult() throws Exception {
        String userId = "testId";
        AsyncResult<JsonObject> result = getAsyncResult(false, null);

        when(session.get("group")).thenReturn(null);
        when(httpServerRequest.getParam("id")).thenReturn(userId);
        when(userDAL.findUser(eq(userId), anyObject())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
            handler.handle(result);
            return null;
        }).thenReturn(userDAL);

        FindUserHandler findUserHandler = new FindUserHandler(userDAL);
        findUserHandler.handle(routingContext);

        // main purpose of test
        verify(routingContext, times(1)).fail(500);
        // call once userDAL for failed result
        verify(userDAL, times(1)).findUser(eq(userId), anyObject());
    }

    /**
     * Test handler with success async result but without assigned group in result should not put group in session
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithSuccessResultWithoutGroup() throws Exception {
        String userId = "testId";
        AsyncResult<JsonObject> result = getAsyncResult(true, new JsonObject().put("_id", "test"));

        when(session.get("group")).thenReturn(null);
        when(httpServerRequest.getParam("id")).thenReturn(userId);
        when(userDAL.findUser(eq(userId), anyObject())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
            handler.handle(result);
            return null;
        }).thenReturn(userDAL);

        FindUserHandler findUserHandler = new FindUserHandler(userDAL);
        findUserHandler.handle(routingContext);

        // main purpose of test
        verify(session, never()).put(eq("group"), anyString());
        // trigger next handler
        verify(routingContext, times(1)).next();
    }

    /**
     * Test handler with success async result with group should put group name into session
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithSuccessResultWithGroup() throws Exception {
        String userId = "testId";
        String groupName = "testGroupName";
        AsyncResult<JsonObject> result = getAsyncResult(true, new JsonObject().put("_id", "test").put("groupName", groupName));

        when(session.get("group")).thenReturn(null);
        when(httpServerRequest.getParam("id")).thenReturn(userId);
        when(userDAL.findUser(eq(userId), anyObject())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
            handler.handle(result);
            return null;
        }).thenReturn(userDAL);

        FindUserHandler findUserHandler = new FindUserHandler(userDAL);
        findUserHandler.handle(routingContext);

        // main purpose of test
        verify(session, times(1)).put(eq("group"), eq(groupName));
        // trigger next handler
        verify(routingContext, times(1)).next();
    }
}